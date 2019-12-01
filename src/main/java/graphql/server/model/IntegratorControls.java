/*
 * ******************************************************************************
 *  WARNING: EXPORT CONTROLLED - EAR
 *  THESE ITEM(S) / TECHNICAL DATA CONTAIN INFORMATION SUBJECT TO U.S.
 *  GOVERNMENT EXPORT CONTROL IN ACCORDANCE WITH THE EXPORT ADMINISTRATION
 *  REGULATIONS (EAR), 15 CFR PARTS 730-774. EXPORT OF THIS DATA TO ANY
 *  FOREIGN COUNTRY OR DISCLOSURE OF THIS DATA TO ANY NON-US PERSON MAY BE A
 *  VIOLATION OF FEDERAL LAW.
 * ******************************************************************************
 *  Unlimited Government Rights
 *  WARNING: Do Not Use On A Privately Funded Program Without Permission.
 * ******************************************************************************
 *  CLASSIFICATION:   Unclassified
 *
 *  LIMITATIONS:      None
 * ******************************************************************************
 */
package graphql.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class IntegratorControls implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier
     */
    @Id
    private String id;


    /**
     * Associated satellite
     */
    @NotNull
    private  String satelliteId;

    /**
     * Enumeration to indicate if the controls are associated with a specific application
     */
    @NotNull
    private IcApplication application;

    /**
     * Identifies the integrator controls coordinate system
     */
    @NotNull
    private IcCoordinateSystem coordinateSystem;

    /**
     * Error Control
     */
    @NotNull
    private Double errorControl;

    /**
     * Input Step Size (seconds).
     */
    @NotNull
    private Double inputStepSize;

    /**
     * Partial Derivatives enumeration
     */
    @NotNull
    private PartialDerivatives partialDerivatives;

    /**
     * Print Step Change Flag
     */
    @NotNull
    private Boolean printStepChangeFlag;

    /**
     * Propagator enumeration
     */
    @NotNull
    private Propagator propagator;

    /**
     * SPADOC Emulation Flag
     */
    @NotNull
    private Boolean spadocEmulationFlag;

    /**
     * Step Mode enumeration
     */
    @NotNull
    private StepMode stepMode;

    /**
     * Step Size Method enumeration
     */
    @NotNull
    private StepSizeMethod stepSizeMethod;

    /**
     * Step Size Source enumeration
     */
    @NotNull
    private StepSizeSource stepSizeSource;
}
