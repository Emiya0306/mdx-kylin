/*
  Copyright (C) 2021 Kyligence Inc. All rights reserved.

  http://kyligence.io

  This software is the confidential and proprietary information of
  Kyligence Inc. ("Confidential Information"). You shall not disclose
  such Confidential Information and shall use it only in accordance
  with the terms of the license agreement you entered into with
  Kyligence Inc.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/* eslint-disable newline-per-chained-call */
const { By, until } = require('selenium-webdriver');
const assert = require('assert');
const { createDriver, getLaunchUrl } = require('../utils/startup');
const { login, logout } = require('../utils/businessHelper');
const { waitingForLoading } = require('../utils/domHelper');
const { clickOn } = require('../utils/actionHelper');

const {
  USERNAME_ADMIN,
  PASSWORD_ADMIN,
} = process.env;

/* eslint-disable newline-per-chained-call */
describe('参数配置界面', async function () {
  this.timeout(300000);
  let driver;

  before(async () => {
    driver = await createDriver();
  });

  after(async () => {
    await driver.quit();
  });

  it('进入参数配置页面', async () => {
    await login(driver, USERNAME_ADMIN, PASSWORD_ADMIN);

    await driver.findElement(By.css('.page-header-right div:nth-child(1) button')).click();
    await driver.wait(until.stalenessOf(await driver.findElement(By.css('.global-admin-mask'))), 10000);
    await driver.findElement(By.css('.ant-menu li[link="/configuration"]')).click();
    await driver.wait(until.elementLocated(By.css('.configurations')), 10000);

    assert.equal(await driver.getCurrentUrl(), `${getLaunchUrl()}/configuration`);
  });

  it('重启同步任务', async () => {
    await driver.findElement(By.css('.configuration:nth-child(4) .el-button')).click();
    await waitingForLoading(driver, '.block-body');
  });

  it('修改连接用户', async () => {
    await clickOn(driver, By.css('.configuration:nth-child(5) .el-button'));
    await driver.wait(until.elementLocated(By.css('.connection-user-modal')), 10000);

    await driver.findElement(By.css('.connection-user-modal .input-username input')).sendKeys(USERNAME_ADMIN);
    await driver.findElement(By.css('.connection-user-modal .input-password input')).sendKeys(PASSWORD_ADMIN);
    await driver.findElement(By.css('.connection-user-modal .dialog-footer .el-button--primary')).click();

    await driver.wait(until.elementIsNotVisible(await driver.findElement(By.css('.connection-user-modal'))), 10000);

    await logout(driver);
  });
});