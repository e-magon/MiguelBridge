/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix;

public class ThreePidMapping implements _ThreePidMapping {

    private _ThreePid threePid;
    private _MatrixID mxId;

    public ThreePidMapping(_ThreePid threePid, _MatrixID mxId) {
        this.threePid = threePid;
        this.mxId = mxId;
    }

    @Override
    public _ThreePid getThreePid() {
        return threePid;
    }

    @Override
    public _MatrixID getMatrixId() {
        return mxId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreePidMapping that = (ThreePidMapping) o;

        if (!threePid.equals(that.threePid)) return false;
        return mxId.equals(that.mxId);
    }

    @Override
    public int hashCode() {
        int result = threePid.hashCode();
        result = 31 * result + mxId.hashCode();
        return result;
    }

}
