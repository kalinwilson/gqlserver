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

import com.google.common.collect.Lists;

import java.util.List;

public enum IcApplication
{
    NotAssociated,
    ROB,
    DCA;

    public static IcApplication getByIntValue(int value) {
        return IcApplication.values()[value];
    }

    public static List<IcApplication> getValues(List<String> applicationStrings) {
        List<IcApplication> applications = Lists.newArrayList();
        for(String appString : applicationStrings){
            try {
                applications.add(IcApplication.valueOf(appString));
            } catch(Throwable e){
                continue; // ignore bad values
            }
        }
        return applications;
    }
}
