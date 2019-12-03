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
import lombok.NonNull;

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
public class ElementConversionControls implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * unique identifier
     */
    @Id
    private String id;

    /**
     * Associated satellite
     */
    @NotNull
    private String satelliteId;

    /**
     * Enumeration to indicate if the controls are associated with a specific application
     */
    @NotNull
    private ApplicationEnum application;

    /**
     * Epoch Placement
     */
    @NotNull
    private EpochPlacement epochPlacement;

    /**
     * Extrapolation DC Delta Span
     */
    @NotNull
    private Double extrapolationDcDeltaSpan;

    /**
     * Extrapolation DC Drag ER RMS
     */
    @NotNull
    private Double extrapolationDcDragErRms;

    /**
     * Indicates whether or not to perform extrapolation
     */
    @NotNull
    @NonNull
    private Boolean extrapolationDc;

    /**
     * Extrapolation DC Epoch ER RMS
     */
    @NotNull
    private Double extrapolationDcEpochErRms;

    /**
     * Extrapolation DC Epoch Margin
     */
    @NotNull
    private Double extrapolationDcEpochMargin;

    /**
     * Extrapolation DC Keep Nth Point
     */
    @NotNull
    private Integer extrapolationDcKeepNthPnt;

    /**
     * Extrapolation DC Max Position RMS
     */
    @NotNull
    private Double extrapolationDcMaxPosRms;

    /**
     * Extrapolation DC Minimum Span
     */
    @NotNull
    private Double extrapolationDcMinSpan;

    /**
     * Extrapolation DC Span
     */
    @NotNull
    private Double extrapolationDcSpan;

    /**
     * Extrapolation span units
     */
    @NotNull
    private ExtrapolationSpanUnits extrapolationSpanUnits;
}
