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

import org.patryk3211.powergrid.electricity.sim.AbstractElectricWire;
import org.patryk3211.powergrid.electricity.sim.node.IElectricNode;

class PNJunction extends AbstractElectricWire {
    public static final double V_T = 0.0257; // thermal voltage at 25C
    public static final double I_s = 1e-5; // reverse saturation current
    public static final double R_s = 1.0; // series resistance

    private double currentConductance;
    public double dV;

    public PNJunction(IElectricNode p, IElectricNode n) {
        super(p, n);
    }

    /**
     * Lambert W(z) function - Series approximation
     * ref: implemented from Python code found somewhere in the web
     * @param z
     * @return
     */
    public static double LambertW(double z)
    {
        double PRECISION = 1E-12;
        double S = 0.0;
        for (int n=1; n <= 100; n++)
        {
            double Se = S * StrictMath.pow(StrictMath.E, S);
            double S1e = (S+1) *
                    StrictMath.pow(StrictMath.E, S);
            if (PRECISION > StrictMath.abs((z-Se)/S1e))
            {
                return S;
            }
            S -=
                    (Se-z) / (S1e - (S+2) * (Se-z) / (2*S+2));
        }
        return S;
    }

    public static double gm(double V) {
        // take derivative of Banwell and Jayakumar (2000)
        double IsRs = I_s * R_s;
        double WTerm = LambertW((IsRs + StrictMath.exp((IsRs + V) / V_T )) / V_T);
        return WTerm / (R_s * (WTerm + 1));
    }

    public void updateConductance(double newConductance) {
        network.updateConductance(this, newConductance - currentConductance);
        currentConductance = newConductance;
    }

    @Override
    public double conductance() {
        return currentConductance;
    }
}
