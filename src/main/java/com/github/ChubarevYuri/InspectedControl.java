package com.github.ChubarevYuri;

/**
 * inspected control for objects.
 */
public interface InspectedControl {

    /**
     * @return used object in inspection.
     */
    boolean isInspection() throws DeviceInterfaceException;

    /**
     * @param v used object in inspection.
     */
    void setInspection(boolean v) throws PortException;
}
