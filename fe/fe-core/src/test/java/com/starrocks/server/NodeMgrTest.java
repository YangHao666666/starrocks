// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.starrocks.server;

import com.starrocks.ha.FrontendNodeType;
import com.starrocks.system.Frontend;
import org.junit.Assert;
import org.junit.Test;

import java.net.UnknownHostException;

public class NodeMgrTest {

    @Test(expected = UnknownHostException.class)
    public void testCheckFeExistByIpOrFqdnException() throws UnknownHostException {
        NodeMgr nodeMgr = new NodeMgr(false, GlobalStateMgr.getCurrentState());
        nodeMgr.checkFeExistByIpOrFqdn("not-exist-host.com");
    }

    @Test
    public void testCheckFeExistByIpOrFqdn() throws UnknownHostException {
        NodeMgr nodeMgr = new NodeMgr(false, GlobalStateMgr.getCurrentState());
        nodeMgr.replayAddFrontend(new Frontend(FrontendNodeType.FOLLOWER, "node1", "localhost", 9010));
        Assert.assertTrue(nodeMgr.checkFeExistByIpOrFqdn("localhost"));
        Assert.assertTrue(nodeMgr.checkFeExistByIpOrFqdn("127.0.0.1"));
    }
}
