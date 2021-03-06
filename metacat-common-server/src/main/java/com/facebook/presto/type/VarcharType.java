/*
 * Copyright 2016 Netflix, Inc.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.facebook.presto.type;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.facebook.presto.spi.ConnectorSession;
import com.facebook.presto.spi.block.Block;
import com.facebook.presto.spi.block.BlockBuilder;
import com.facebook.presto.spi.type.AbstractVariableWidthType;
import com.facebook.presto.spi.type.StandardTypes;
import com.facebook.presto.spi.type.TypeSignature;
import com.google.common.collect.Lists;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import java.util.Collections;

/**
 * Varchar type.
 */
public final class VarcharType
    extends AbstractVariableWidthType {
    /** Default varchar type. */
    public static final VarcharType VARCHAR = new VarcharType(1);

    private final int length;

    private VarcharType(final int length) {
        super(
            new TypeSignature(
                StandardTypes.VARCHAR, Lists.newArrayList(),
                Collections.singletonList((long) length)),
            Slice.class);

        if (length < 0) {
            throw new IllegalArgumentException("Invalid VARCHAR length " + length);
        }
        this.length = length;
    }
    /**
     * Cretes varchar type.
     * @param length length
     * @return VarcharType
     */
    public static VarcharType createVarcharType(final int length) {
        return new VarcharType(length);
    }

    public int getLength() {
        return length;
    }

    @Override
    public boolean isComparable() {
        return true;
    }

    @Override
    public boolean isOrderable() {
        return true;
    }

    @Override
    public Object getObjectValue(final ConnectorSession session, final Block block, final int position) {
        if (block.isNull(position)) {
            return null;
        }

        return block.getSlice(position, 0, block.getLength(position)).toStringUtf8();
    }

    @Override
    public boolean equalTo(final Block leftBlock, final int leftPosition, final Block rightBlock,
        final int rightPosition) {
        final int leftLength = leftBlock.getLength(leftPosition);
        final int rightLength = rightBlock.getLength(rightPosition);
        if (leftLength != rightLength) {
            return false;
        }
        return leftBlock.equals(leftPosition, 0, rightBlock, rightPosition, 0, leftLength);
    }

    @Override
    public int hash(final Block block, final int position) {
        return block.hash(position, 0, block.getLength(position));
    }

    @Override
    public int compareTo(final Block leftBlock, final int leftPosition, final Block rightBlock,
        final int rightPosition) {
        final int leftLength = leftBlock.getLength(leftPosition);
        final int rightLength = rightBlock.getLength(rightPosition);
        return leftBlock.compareTo(leftPosition, 0, leftLength, rightBlock, rightPosition, 0, rightLength);
    }

    @Override
    public void appendTo(final Block block, final int position, final BlockBuilder blockBuilder) {
        if (block.isNull(position)) {
            blockBuilder.appendNull();
        } else {
            block.writeBytesTo(position, 0, block.getLength(position), blockBuilder);
            blockBuilder.closeEntry();
        }
    }

    @Override
    public Slice getSlice(final Block block, final int position) {
        return block.getSlice(position, 0, block.getLength(position));
    }

    /**
     * Writes string.
     * @param blockBuilder builder
     * @param value value
     */
    public void writeString(final BlockBuilder blockBuilder, final String value) {
        writeSlice(blockBuilder, Slices.utf8Slice(value));
    }

    @Override
    public void writeSlice(final BlockBuilder blockBuilder, final Slice value) {
        writeSlice(blockBuilder, value, 0, value.length());
    }

    @Override
    public void writeSlice(final BlockBuilder blockBuilder, final Slice value, final int offset, final int sLength) {
        blockBuilder.writeBytes(value, offset, sLength).closeEntry();
    }
}

