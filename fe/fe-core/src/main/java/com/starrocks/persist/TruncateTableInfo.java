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

// This file is based on code available under the Apache license here:
//   https://github.com/apache/incubator-doris/blob/master/fe/fe-core/src/main/java/org/apache/doris/persist/TruncateTableInfo.java

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.starrocks.persist;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.starrocks.catalog.Partition;
import com.starrocks.common.FeMetaVersion;
import com.starrocks.common.io.Text;
import com.starrocks.common.io.Writable;
import com.starrocks.persist.gson.GsonUtils;
import com.starrocks.server.GlobalStateMgr;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

public class TruncateTableInfo implements Writable {

    @SerializedName(value = "dbId")
    private long dbId;
    @SerializedName(value = "tblId")
    private long tblId;
    @SerializedName(value = "partitions")
    private List<Partition> partitions = Lists.newArrayList();
    @SerializedName(value = "isEntireTable")
    private boolean isEntireTable = false;

    private TruncateTableInfo() {

    }

    public TruncateTableInfo(long dbId, long tblId, List<Partition> partitions, boolean isEntireTable) {
        this.dbId = dbId;
        this.tblId = tblId;
        this.partitions = partitions;
        this.isEntireTable = isEntireTable;
    }

    public long getDbId() {
        return dbId;
    }

    public long getTblId() {
        return tblId;
    }

    public List<Partition> getPartitions() {
        return partitions;
    }

    public boolean isEntireTable() {
        return isEntireTable;
    }

    public static TruncateTableInfo read(DataInput in) throws IOException {
        if (GlobalStateMgr.getCurrentStateJournalVersion() < FeMetaVersion.VERSION_74) {
            TruncateTableInfo info = new TruncateTableInfo();
            info.readFields(in);
            return info;
        } else {
            String json = Text.readString(in);
            return GsonUtils.GSON.fromJson(json, TruncateTableInfo.class);
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        String json = GsonUtils.GSON.toJson(this);
        Text.writeString(out, json);
    }

    private void readFields(DataInput in) throws IOException {
        dbId = in.readLong();
        tblId = in.readLong();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            Partition partition = Partition.read(in);
            partitions.add(partition);
        }
    }
}
