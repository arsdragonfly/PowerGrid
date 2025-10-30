/*
 * Copyright 2025 patryk3211
 *
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
package org.patryk3211.powergrid.electricity.sim.special;

import org.ejml.data.DMatrixRMaj;
import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;

public class DiodeWire extends DynamicConductanceWire {
    private final float resistance;
    private final float biasVoltage;
    private double prevPotential;
    private double prevCurrent;

    private double In;

    public DiodeWire(float resistance, float biasVoltage, IElectricNode cathode, IElectricNode anode) {
        super(anode, cathode);
        this.resistance = resistance;
        this.biasVoltage = biasVoltage;
    }

    @Override
    public float current() {
        return (float) (super.current() - In);
    }

    @Override
    public float power() {
        return potentialDifference() * current();
    }

    @Override
    public double calculateConductance() {
        double V = super.potentialDifference();

        var gm = PNJunction.gm(V);
        return StrictMath.max(gm, 1e-9);
    }

    @Override
    public void postUpperSolve() {
//        In = 0;
//        prevCurrent = 0;
//        prevPotential = 0;
    }

    @Override
    public void addResidual(DMatrixRMaj residual) {
        residual.add(node1.getIndex(), 0, -In);
        residual.add(node2.getIndex(), 0,  In);
    }
}
