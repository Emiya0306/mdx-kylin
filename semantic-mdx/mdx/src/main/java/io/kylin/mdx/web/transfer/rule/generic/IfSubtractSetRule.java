/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.kylin.mdx.web.transfer.rule.generic;

import io.kylin.mdx.web.transfer.rule.MdxTransferRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfSubtractSetRule extends MdxTransferRule {
    private static final Pattern subtractSetPattern = Pattern.compile("(?i)(AddCalculatedMembers[\\s\\S]+?\\))[\\s]*-[\\s]*(\\{[\\s\\S]+?\\})");

    @Override
    public String apply(String mdx) {
        Matcher subtractSetMatcher = subtractSetPattern.matcher(mdx);
        if (subtractSetMatcher.find()) {
            mdx = subtractSetMatcher.replaceFirst("Except(" + subtractSetMatcher.group(1)
                    + "," + subtractSetMatcher.group(2) + ")");
        }
        return mdx;
    }
}