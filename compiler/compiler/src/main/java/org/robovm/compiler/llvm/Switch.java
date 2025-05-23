/*
 * Copyright (C) 2012 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.llvm;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @version $Id$
 */
public class Switch extends Instruction {
    private final Value value;
    private final BasicBlockRef def;
    private final Map<IntegerConstant, BasicBlockRef> alt;

    public Switch(Value value, BasicBlockRef def, IntegerConstant altVal, BasicBlockRef altBlock) {
        this(value, def, Collections.singletonMap(altVal, altBlock));
    }
    
    public Switch(Value value, BasicBlockRef def, Map<IntegerConstant, BasicBlockRef> alt) {
        if (!value.isInteger()) {
            throw new IllegalArgumentException("Integer type expected");
        }
        this.value = value;
        this.def = def;
        this.alt = alt;
    }
    
    @Override
    public Set<VariableRef> getReadsFrom() {
        if (value instanceof VariableRef) {
            return Collections.singleton((VariableRef) value);
        }
        return super.getReadsFrom();
    }
    
    @Override
    public Set<BasicBlockRef> getBranchTargets() {
        Set<BasicBlockRef> result = new HashSet<>();
        result.add(def);
        result.addAll(alt.values());
        return result;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write("switch ");
        value.getType().write(writer);
        writer.write(' ');
        value.write(writer);
        writer.write(", label %");
        writer.write(def.getName());
        writer.write(" [ ");
        for (Entry<IntegerConstant, BasicBlockRef> pair : alt.entrySet()) {
            pair.getKey().getType().write(writer);
            writer.write(' ');
            pair.getKey().write(writer);
            writer.write(", label %");
            writer.write(pair.getValue().getName());
            writer.write(' ');
        }
        writer.write(']');
    }

    @Override
    public String toString() {
        return toString(this::write);
    }
}
