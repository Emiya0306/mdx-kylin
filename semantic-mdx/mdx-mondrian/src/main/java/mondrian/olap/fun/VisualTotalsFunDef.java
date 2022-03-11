/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2006-2013 Pentaho
// All Rights Reserved.
*/
package mondrian.olap.fun;

import mondrian.calc.*;
import mondrian.calc.impl.AbstractListCalc;
import mondrian.calc.impl.UnaryTupleList;
import mondrian.mdx.*;
import mondrian.olap.*;
import mondrian.olap.type.*;
import mondrian.resource.MondrianResource;
import mondrian.rolap.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition of the <code>VisualTotals</code> MDX function.
 *
 * @author jhyde
 * @since Jan 16, 2006
 */
public class VisualTotalsFunDef extends FunDefBase {
    static final Resolver Resolver =
        new ReflectiveMultiResolver(
            "VisualTotals",
            "VisualTotals(<Set>[, <Pattern>])",
            "Dynamically totals child members specified in a set using a pattern for the total label in the result set.",
            new String[] {"fxx", "fxxS"},
            VisualTotalsFunDef.class);

    public VisualTotalsFunDef(FunDef dummyFunDef) {
        super(dummyFunDef);
    }

    protected Exp validateArg(
        Validator validator, Exp[] args, int i, int category)
    {
        final Exp validatedArg =
            super.validateArg(validator, args, i, category);
        if (i == 0) {
            // The function signature guarantees that we have a set of members
            // or a set of tuples.
            final SetType setType = (SetType) validatedArg.getType();
            final Type elementType = setType.getElementType();
            if (!(elementType instanceof MemberType)) {
                throw validator.getPrintStackEx(MondrianResource.instance().VisualTotalsAppliedToTuples
                    .ex());
            }
        }
        return validatedArg;
    }

    public Calc compileCall(ResolvedFunCall call, ExpCompiler compiler) {
        final ListCalc listCalc = compiler.compileList(call.getArg(0));
        final StringCalc stringCalc =
            call.getArgCount() > 1
            ? compiler.compileString(call.getArg(1))
            : null;
        return new CalcImpl(call, listCalc, stringCalc);
    }

    /**
     * Calc implementation of the <code>VisualTotals</code> function.
     */
    private static class CalcImpl extends AbstractListCalc {
        private final ListCalc listCalc;
        private final StringCalc stringCalc;

        public CalcImpl(
            ResolvedFunCall call, ListCalc listCalc, StringCalc stringCalc)
        {
            super(call, new Calc[] {listCalc, stringCalc});
            this.listCalc = listCalc;
            this.stringCalc = stringCalc;
        }

        public TupleList evaluateList(Evaluator evaluator) {
            final List<Member> list =
                listCalc.evaluateList(evaluator).slice(0);
            final List<Member> resultList = new ArrayList<Member>(list);
            final int memberCount = list.size();
            for (int i = memberCount - 1; i >= 0; --i) {
                RolapMember member = (RolapMember) list.get(i);
                if (i + 1 < memberCount) {
                    Member nextMember = resultList.get(i + 1);
                    if (nextMember != member
                        && nextMember.isChildOrEqualTo(member))
                    {
                        resultList.set(
                            i,
                            createMember(member, i, resultList, evaluator));
                    }
                }
            }
            return new UnaryTupleList(resultList);
        }

        private VisualTotalMember createMember(
            RolapMember member,
            int i,
            final List<Member> list,
            Evaluator evaluator)
        {
            final String name;
            if (stringCalc != null) {
                final String namePattern = stringCalc.evaluateString(evaluator);
                name = substitute(namePattern, member.getName());
            } else {
                name = member.getName();
            }
            final List<Member> childMemberList =
                followingDescendants(member, i + 1, list);
            final Exp exp = makeExpr(childMemberList);
            final Validator validator = evaluator.getQuery().createValidator();
            final Exp validatedExp = exp.accept(validator);
            return new VisualTotalMember(member, name, validatedExp, childMemberList);
        }

        private List<Member> followingDescendants(
            Member member, int i, final List<Member> list)
        {
            List<Member> childMemberList = new ArrayList<Member>();
            while (i < list.size()) {
                Member descendant = list.get(i);
                if (descendant.equals(member)) {
                    // strict descendants only
                    break;
                }
                if (!descendant.isChildOrEqualTo(member)) {
                    break;
                }
                if (descendant instanceof VisualTotalMember) {
                    // Add the visual total member, but skip over its children.
                    VisualTotalMember visualTotalMember =
                            (VisualTotalMember) descendant;
                    childMemberList.add(visualTotalMember);
                    i = lastChildIndex(visualTotalMember.member, i, list);
                    continue;
                }
                childMemberList.add(descendant);
                ++i;
            }
            return childMemberList;
        }

        private int lastChildIndex(Member member, int start, List list) {
            int i = start;
            while (true) {
                ++i;
                if (i >= list.size()) {
                    break;
                }
                Member descendant = (Member) list.get(i);
                if (descendant.equals(member)) {
                    // strict descendants only
                    break;
                }
                if (!descendant.isChildOrEqualTo(member)) {
                    break;
                }
            }
            return i;
        }

        private Exp makeExpr(final List childMemberList) {
            Exp[] memberExprs = new Exp[childMemberList.size()];
            for (int i = 0; i < childMemberList.size(); i++) {
                final Member childMember = (Member) childMemberList.get(i);
                memberExprs[i] = new MemberExpr(childMember);
            }
            return new UnresolvedFunCall(
                "Aggregate",
                new Exp[] {
                    new UnresolvedFunCall(
                        "{}",
                        Syntax.Braces,
                        memberExprs)
                });
        }
    }

    /**
     * Calculated member for <code>VisualTotals</code> function.
     *
     * <p>It corresponds to a real member, and most of its properties are
     * similar. The main differences are:<ul>
     * <li>its name is derived from the VisualTotals pattern, e.g.
     *     "*Subtotal - Dairy" as opposed to "Dairy"
     * <li>its value is a calculation computed by aggregating all of the
     *     members which occur following it in the list</ul></p>
     */
    public static class VisualTotalMember extends RolapMemberBase {
        private final RolapMember member;
        private Exp exp;
        private List<Member> childMemberList;

        public VisualTotalMember(
                RolapMember member,
                String name,
                final Exp exp,
                List<Member> childMemberList)
        {
            super(
                member.getParentMember(),
                member.getLevel(),
                null,
                MemberType.FORMULA,
                member.getUniqueName(),
                Larders.ofName(name));
            this.member = member;
            this.exp = exp;
            this.childMemberList = childMemberList;
        }

        @Override
        public boolean equals(Object o) {
            // A visual total member must compare equal to the member it wraps
            // (for purposes of the MDX Intersect function, for instance).
            return o instanceof VisualTotalMember
                && this.member.equals(((VisualTotalMember) o).member)
                && this.exp.equals(((VisualTotalMember) o).exp)
                || o instanceof Member
                && this.member.equals(o);
        }

        @Override
        public int hashCode() {
            return member.hashCode();
        }

        @Override
        public Larder getLarder() {
            // Use underlying member's larder, therefore use its caption.
            return member.getLarder();
        }

        protected boolean computeCalculated(final MemberType memberType) {
            return true;
        }

        public int getSolveOrder() {
            // high solve order, so it is expanded after other calculations
            return MondrianProperties.instance().VisualTotalSolveOrder.get();
        }

        public List<Member> getChildMemberList() {
            List<Member> children = new ArrayList<>(4);
            if (childMemberList == null) {
                return children;
            }

            for (Member member : childMemberList) {
                if (member instanceof VisualTotalMember) {
                    children.addAll(((VisualTotalMember) member).getChildMemberList());
                } else {
                    children.add(member);
                }
            }
            return children;
        }

        public Exp getExpression() {
            return exp;
        }

        public void setExpression(Exp exp) {
            this.exp = exp;
        }

        public void setExpression(
            Evaluator evaluator,
            List<Member> childMembers)
        {
            final Exp exp = makeExpr(childMembers);
            final Validator validator = evaluator.getQuery().createValidator();
            final Exp validatedExp = exp.accept(validator);
            setExpression(validatedExp);
        }

        private Exp makeExpr(final List childMemberList) {
            Exp[] memberExprs = new Exp[childMemberList.size()];
            for (int i = 0; i < childMemberList.size(); i++) {
                final Member childMember = (Member) childMemberList.get(i);
                memberExprs[i] = new MemberExpr(childMember);
            }
            return new UnresolvedFunCall(
                "Aggregate",
                new Exp[] {
                    new UnresolvedFunCall(
                        "{}",
                        Syntax.Braces,
                        memberExprs)
                });
        }

        public int getOrdinal() {
            return member.getOrdinal();
        }

        public RolapMember getDataMember() {
            return member;
        }

        public String getQualifiedName() {
            throw new UnsupportedOperationException();
        }

        public RolapMember getMember() {
            return member;
        }

        public Object getPropertyValue(Property property) {
            switch (property.ordinal) {
            case Property.CHILDREN_CARDINALITY_ORDINAL:
                return member.getPropertyValue(property);
            default:
                return super.getPropertyValue(property);
            }
        }
    }

    /**
     * Substitutes a name into a pattern.<p/>
     *
     * Asterisks are replaced with the name,
     * double-asterisks are replaced with a single asterisk.
     * For example,
     *
     * <blockquote><code>substitute("** Subtotal - *",
     * "Dairy")</code></blockquote>
     *
     * returns
     *
     * <blockquote><code>"* Subtotal - Dairy"</code></blockquote>
     *
     * @param namePattern Pattern
     * @param name Name to substitute into pattern
     * @return Substituted pattern
     */
    static String substitute(String namePattern, String name) {
        final StringBuilder buf = new StringBuilder(256);
        final int namePatternLen = namePattern.length();
        int startIndex = 0;

        while (true) {
            int endIndex = namePattern.indexOf('*', startIndex);

            if (endIndex == -1) {
                // No '*' left
                // append the rest of namePattern from startIndex onwards
                buf.append(namePattern.substring(startIndex));
                break;
            }

            // endIndex now points to the '*'; check for '**'
            ++endIndex;
            if (endIndex < namePatternLen
                && namePattern.charAt(endIndex) == '*')
            {
                // Found '**', replace with '*'
                 // Include first '*'.
                buf.append(namePattern.substring(startIndex, endIndex));
                // Skip over 2nd '*'
                ++endIndex;
            } else {
                // Found single '*' - substitute (omitting the '*')
                // Exclude '*'
                buf.append(namePattern.substring(startIndex, endIndex - 1));
                buf.append(name);
            }

            startIndex = endIndex;
        }

        return buf.toString();
    }

}

// End VisualTotalsFunDef.java
