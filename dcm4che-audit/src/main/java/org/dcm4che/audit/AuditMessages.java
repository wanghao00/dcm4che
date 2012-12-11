/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/gunterze/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4che.audit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 *
 */
public class AuditMessages {

    private static final Pattern IP4 =
            Pattern.compile("\\d+(\\.\\d+){3}");
    private static final Pattern IP6 =
            Pattern.compile("[0-9a-fA-F]*(\\:[0-9a-fA-F]*){7}");
    public static final String SCHEMA_URI =
            "http://www.dcm4che.org/DICOM/audit-message.rnc";

    public static final String PROCESS_ID;
    public static final String MACHINE_NAME;
    static {
        String s = ManagementFactory.getRuntimeMXBean().getName();
        int index = s.indexOf('@');
        PROCESS_ID = s.substring(0, index);
        MACHINE_NAME = s.substring(index + 1);
    }

    private static final ObjectFactory of = new ObjectFactory();
    private static JAXBContext jc;

    private static JAXBContext jc() throws JAXBException {
        JAXBContext jc = AuditMessages.jc;
        if (jc == null)
            AuditMessages.jc = jc = JAXBContext.newInstance(AuditMessage.class);
        return jc;
    }

    public static boolean isIP(String s) {
        return IP4.matcher(s).matches() || IP6.matcher(s).matches();
    }

    public static final class EventActionCode {
        public static final String Create = "C";
        public static final String Read = "R";
        public static final String Update = "U";
        public static final String Delete = "D";
        public static final String Execute = "E";
    }

    public static final class EventOutcomeIndicator {
        public static final String Success = "0";
        public static final String MinorFailure = "4";
        public static final String SeriousFailure = "8";
        public static final String MajorFailure = "12";
    }

    public static final class EventID extends org.dcm4che.audit.EventID {

        public static final EventID ApplicationActivity =
                new EventID("110100", "DCM", "Application Activity");
        public static final EventID AuditLogUsed = 
                new EventID("110101", "DCM", "Audit Log Used");
        public static final EventID BeginTransferringDICOMInstances = 
                new EventID("110102", "DCM", "Begin Transferring DICOM Instances");
        public static final EventID DICOMInstancesAccessed = 
                new EventID("110103", "DCM", "DICOM Instances Accessed");
        public static final EventID DICOMInstancesTransferred = 
                new EventID("110104", "DCM", "DICOM Instances Transferred");
        public static final EventID DICOMStudyDeleted = 
                new EventID("110105", "DCM", "DICOM Study Deleted");
        public static final EventID Export =
                new EventID("110106", "DCM", "Export");
        public static final EventID Import =
                new EventID("110107", "DCM", "Import");
        public static final EventID NetworkEntry = 
                new EventID("110108", "DCM", "Network Entry");
        public static final EventID OrderRecord = 
                new EventID("110109", "DCM", "Order Record");
        public static final EventID PatientRecord = 
                new EventID("110110", "DCM", "Patient Record");
        public static final EventID ProcedureRecord = 
                new EventID("110111", "DCM", "Procedure Record");
        public static final EventID Query =
                new EventID("110112", "DCM", "Query");
        public static final EventID SecurityAlert = 
                new EventID("110113", "DCM", "Security Alert");
        public static final EventID UserAuthentication = 
                new EventID("110114", "DCM", "User Authentication");
        public static final EventID HealthServicesProvisionEvent = 
                new EventID("IHE0001", "IHE", "Health Services Provision Event");
        public static final EventID MedicationEvent = 
                new EventID("IHE0002", "IHE", "Medication Event");
        public static final EventID PatientCareResourceAssignment = 
                new EventID("IHE0003", "IHE", "Patient Care Resource Assignment");
        public static final EventID PatientCareEpisode = 
                new EventID("IHE0004", "IHE", "Patient Care Episode");
        public static final EventID PatientCareProtocol = 
                new EventID("IHE0005", "IHE", "Patient Care Protocol");

        EventID(String code, String codeSystemName, String displayName) {
            super.code = code;
            super.codeSystemName = codeSystemName;
            super.displayName = displayName;
        }

        @Override
        public void setCode(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDisplayName(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOriginalText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystem(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystemName(String value) {
            throw new UnsupportedOperationException();
        }
    }

    public static final class EventTypeCode
            extends org.dcm4che.audit.EventTypeCode {

        public static final EventTypeCode ApplicationStart = 
                new EventTypeCode("110120", "DCM", "Application Start");
        public static final EventTypeCode ApplicationStop = 
                new EventTypeCode("110121", "DCM", "Application Stop");
        public static final EventTypeCode Login = 
                new EventTypeCode("110122", "DCM", "Login");
        public static final EventTypeCode Logout = 
                new EventTypeCode("110123", "DCM", "Logout");
        public static final EventTypeCode Attach = 
                new EventTypeCode("110124", "DCM", "Attach");
        public static final EventTypeCode Detach = 
                new EventTypeCode("110125", "DCM", "Detach");
        public static final EventTypeCode NodeAuthentication = 
                new EventTypeCode("110126", "DCM", "Node Authentication");
        public static final EventTypeCode EmergencyOverrideStarted = 
                new EventTypeCode("110127", "DCM", "Emergency Override Started");
        public static final EventTypeCode NetworkConfiguration = 
                new EventTypeCode("110128", "DCM", "Network Configuration");
        public static final EventTypeCode SecurityConfiguration = 
                new EventTypeCode("110129", "DCM", "Security Configuration");
        public static final EventTypeCode HardwareConfiguration = 
                new EventTypeCode("110130", "DCM", "Hardware Configuration");
        public static final EventTypeCode SoftwareConfiguration = 
                new EventTypeCode("110131", "DCM", "Software Configuration");
        public static final EventTypeCode UseOfRestrictedFunction = 
                new EventTypeCode("110132", "DCM", "Use of Restricted Function");
        public static final EventTypeCode AuditRecordingStopped = 
                new EventTypeCode("110133", "DCM", "Audit Recording Stopped");
        public static final EventTypeCode AuditRecordingStarted = 
                new EventTypeCode("110134", "DCM", "Audit Recording Started");
        public static final EventTypeCode ObjectSecurityAttributesChanged = 
                new EventTypeCode("110135", "DCM", "Object Security Attributes Changed");
        public static final EventTypeCode SecurityRolesChanged = 
                new EventTypeCode("110136", "DCM", "Security Roles Changed");
        public static final EventTypeCode UserSecurityAttributesChanged = 
                new EventTypeCode("110137", "DCM", "User security Attributes Changed");
        public static final EventTypeCode EmergencyOverrideStopped = 
                new EventTypeCode("110138", "DCM", "Emergency Override Stopped");
        public static final EventTypeCode RemoteServiceOperationStarted = 
                new EventTypeCode("110139", "DCM", "Remote Service Operation Started");
        public static final EventTypeCode RemoteServiceOperationStopped = 
                new EventTypeCode("110140", "DCM", "Remote Service Operation Stopped");
        public static final EventTypeCode LocalServiceOperationStarted = 
                new EventTypeCode("110141", "DCM", "Local Service Operation Started");
        public static final EventTypeCode LocalServiceOperationStopped = 
                new EventTypeCode("110142", "DCM", "Local Service Operation Stopped");

        public EventTypeCode(String code, String codeSystemName,
                String displayName) {
            super.code = code;
            super.codeSystemName = codeSystemName;
            super.displayName = displayName;
        }

        @Override
        public void setCode(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDisplayName(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOriginalText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystem(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystemName(String value) {
            throw new UnsupportedOperationException();
        }

    }

    public static final class AuditSourceTypeCode
            extends org.dcm4che.audit.AuditSourceTypeCode {

        public static final AuditSourceTypeCode EndUserDisplayDevice = 
                new AuditSourceTypeCode("1");
        public static final AuditSourceTypeCode DataAcquisitionDevice = 
                new AuditSourceTypeCode("2");
        public static final AuditSourceTypeCode WebServerProcess = 
                new AuditSourceTypeCode("3");
        public static final AuditSourceTypeCode ApplicationServerProcess = 
                new AuditSourceTypeCode("4");
        public static final AuditSourceTypeCode DatabaseServerProcess = 
                new AuditSourceTypeCode("5");
        public static final AuditSourceTypeCode SecurityServer = 
                new AuditSourceTypeCode("6");
        public static final AuditSourceTypeCode NetworkComponent = 
                new AuditSourceTypeCode("7");
        public static final AuditSourceTypeCode OperatingSoftware = 
                new AuditSourceTypeCode("8");
        public static final AuditSourceTypeCode Other = 
                new AuditSourceTypeCode("9");

        public AuditSourceTypeCode(String code) {
            super.code = code;
        }

        public AuditSourceTypeCode(String code, String codeSystemName,
                String displayName) {
            super.code = code;
            super.codeSystemName = codeSystemName;
            super.displayName = displayName;
        }

        @Override
        public void setCode(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDisplayName(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOriginalText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystem(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystemName(String value) {
            throw new UnsupportedOperationException();
        }

    }

    public static final class RoleIDCode extends org.dcm4che.audit.RoleIDCode {

        public static final RoleIDCode Application = 
                new RoleIDCode("110150","DCM","Application");
        public static final RoleIDCode ApplicationLauncher = 
                new RoleIDCode("110151","DCM","Application Launcher");
        public static final RoleIDCode Destination = 
                new RoleIDCode("110152","DCM","Destination");
        public static final RoleIDCode Source = 
                new RoleIDCode("110153","DCM","Source");
        public static final RoleIDCode DestinationMedia = 
                new RoleIDCode("110154","DCM","Destination Media");
        public static final RoleIDCode SourceMedia = 
                new RoleIDCode("110155","DCM","Source Media");

        public RoleIDCode(String code, String codeSystemName,
                String displayName) {
            super.code = code;
            super.codeSystemName = codeSystemName;
            super.displayName = displayName;
        }

        @Override
        public void setCode(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDisplayName(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOriginalText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystem(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystemName(String value) {
            throw new UnsupportedOperationException();
        }

    }

    public static final class MediaType extends org.dcm4che.audit.MediaType {

        public final MediaType USBDiskEmulation =
                new MediaType("110030", "DCM", "USB Disk Emulation");
        public final MediaType Email =
                new MediaType("110031", "DCM", "Email");
        public final MediaType CD =
                new MediaType("110032", "DCM", "CD");
        public final MediaType DVD =
                new MediaType("110033", "DCM", "DVD");
        public final MediaType CompactFlash =
                new MediaType("110034", "DCM", "Compact Flash");
        public final MediaType MultiMediaCard =
                new MediaType("110035", "DCM", "Multi-media Card");
        public final MediaType SecureDigitalCard =
                new MediaType("110036", "DCM", "Secure Digital Card");
        public final MediaType URI =
                new MediaType("110037", "DCM", "URI");
        public final MediaType Film =
                new MediaType("110010", "DCM", "Film");
        public final MediaType PaperDocument =
                new MediaType("110038", "DCM", "Paper Document");

        public MediaType(String code, String codeSystemName,
                String displayName) {
            super.code = code;
            super.codeSystemName = codeSystemName;
            super.displayName = displayName;
        }

        @Override
        public void setCode(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDisplayName(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOriginalText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystem(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystemName(String value) {
            throw new UnsupportedOperationException();
        }

    }

    public static final class NetworkAccessPointTypeCode {
        public static final String MachineName = "1";
        public static final String IPAddress = "2";
        public static final String TelephoneNumber = "3";
        public static final String EmailAddress = "4";
        public static final String URI = "5";
    }

    public static final class ParticipantObjectTypeCode {
        public static final String Person = "1";
        public static final String SystemObject = "2";
        public static final String Organization = "3";
        public static final String Other = "4";
    }

    public static final class ParticipantObjectTypeCodeRole {
        public static final String Patient = "1";
        public static final String Location = "2";
        public static final String Report = "3";
        public static final String Resource = "4";
        public static final String MasterFile = "5";
        public static final String User = "6";
        public static final String List = "7";
        public static final String Doctor = "8";
        public static final String Subscriber = "9";
        public static final String Guarantor = "10";
        public static final String SecurityUserEntity = "11";
        public static final String SecurityUserGroup = "12";
        public static final String SecurityResource = "13";
        public static final String SecurityGranualarityDefinition = "14";
        public static final String Provider = "15";
        public static final String ReportDestination = "16";
        public static final String ReportLibrary = "17";
        public static final String Schedule = "18";
        public static final String Customer = "19";
        public static final String Job = "20";
        public static final String JobStream = "21";
        public static final String Table = "22";
        public static final String RoutingCriteria = "23";
        public static final String Query = "24";
    }

    public static final class ParticipantObjectDataLifeCycle {
        public static final String OriginationCreation = "1";
        public static final String ImportCopyFromOriginal  = "2";
        public static final String Amendment = "3";
        public static final String Verification = "4";
        public static final String Translation = "5";
        public static final String AccessUse = "6";
        public static final String DeIdentification = "7";
        public static final String AggregationSummarizationDerivation = "8";
        public static final String Report = "9";
        public static final String ExportCopyToTarget = "10";
        public static final String Disclosure = "11";
        public static final String ReceiptOfDisclosure = "12";
        public static final String Archiving = "13";
        public static final String LogicalDeletion = "14";
        public static final String PermanentErasurePhysicalDestruction  = "15";
    }

    public static final class ParticipantObjectIDTypeCode
            extends org.dcm4che.audit.ParticipantObjectIDTypeCode {

        public static final ParticipantObjectIDTypeCode MedicalRecordNumber = 
                new ParticipantObjectIDTypeCode("1");
        public static final ParticipantObjectIDTypeCode PatientNumber =
                new ParticipantObjectIDTypeCode("2");
        public static final ParticipantObjectIDTypeCode EncounterNumber =
                new ParticipantObjectIDTypeCode("3");
        public static final ParticipantObjectIDTypeCode EnrolleeNumber =
                new ParticipantObjectIDTypeCode("4");
        public static final ParticipantObjectIDTypeCode SocialSecurityNumber = 
                new ParticipantObjectIDTypeCode("5");
        public static final ParticipantObjectIDTypeCode AccountNumber =
                new ParticipantObjectIDTypeCode("6");
        public static final ParticipantObjectIDTypeCode GuarantorNumber =
                new ParticipantObjectIDTypeCode("7");
        public static final ParticipantObjectIDTypeCode ReportName =
                new ParticipantObjectIDTypeCode("8");    
        public static final ParticipantObjectIDTypeCode ReportNumber =
                new ParticipantObjectIDTypeCode("9");
        public static final ParticipantObjectIDTypeCode SearchCriteria =
                new ParticipantObjectIDTypeCode("10");
        public static final ParticipantObjectIDTypeCode UserIdentifier =
                new ParticipantObjectIDTypeCode("11");
        public static final ParticipantObjectIDTypeCode URI =
                new ParticipantObjectIDTypeCode("12");
        public static final ParticipantObjectIDTypeCode StudyInstanceUID = 
                new ParticipantObjectIDTypeCode("110180","DCM","Study Instance UID");
        public static final ParticipantObjectIDTypeCode SOPClassUID = 
                new ParticipantObjectIDTypeCode("110181","DCM","SOP Class UID");
        public static final ParticipantObjectIDTypeCode NodeID = 
                new ParticipantObjectIDTypeCode("110182","DCM","Node ID");

        public ParticipantObjectIDTypeCode(String code) {
            super.code = code;
        }

        public ParticipantObjectIDTypeCode(String code, String codeSystemName,
                String displayName) {
            super.code = code;
            super.codeSystemName = codeSystemName;
            super.displayName = displayName;
        }

        @Override
        public void setCode(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDisplayName(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOriginalText(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystem(String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCodeSystemName(String value) {
            throw new UnsupportedOperationException();
        }

    }

    public static EventIdentification createEventIdentification(
            EventID eventID, String action, Calendar eventDateTime,
            String outcome, String outcomeDescription, EventTypeCode... types) {
        EventIdentification ei = new EventIdentification();
        ei.setEventID(eventID);
        ei.setEventDateTime(
                eventDateTime != null ? eventDateTime : Calendar.getInstance());
        ei.setEventActionCode(action);
        ei.setEventOutcomeIndicator(outcome);
        ei.setEventOutcomeDescription(outcomeDescription);
        for (EventTypeCode type : types)
            ei.getEventTypeCode().add(type);
        return ei;
    }

    public static ActiveParticipant createActiveParticipant(
            String userID, String altUserID, String name, boolean requestor,
            String napID, String napTypeCode, MediaType mediaType,
            RoleIDCode... roleIDs) {
        ActiveParticipant ap = new ActiveParticipant();
        ap.setUserID(userID);
        ap.setAlternativeUserID(altUserID);
        ap.setUserName(name);
        ap.setUserIsRequestor(requestor);
        ap.setNetworkAccessPointID(napID);
        ap.setNetworkAccessPointTypeCode(napTypeCode);
        ap.setMediaType(mediaType);
        for (RoleIDCode roleID : roleIDs)
            ap.getRoleIDCode().add(roleID);
        return ap;
    }

   public static AuditSourceIdentification createAuditSourceIdentification(
            String siteID, String sourceID, AuditSourceTypeCode... types) {
        AuditSourceIdentification asi = new AuditSourceIdentification();
        asi.setAuditEnterpriseSiteID(siteID);
        asi.setAuditSourceID(sourceID);
        for (AuditSourceTypeCode type : types)
            asi.getAuditSourceTypeCode().add(type);
        return asi;
   }

    public static ParticipantObjectIdentification createParticipantObjectIdentification(
            String id, ParticipantObjectIDTypeCode idType, String name,
            byte[] query, String type, String role, String lifeCycle,
            String sensitivity, ParticipantObjectDescription description,
            ParticipantObjectDetail... details) {
        ParticipantObjectIdentification poi = new ParticipantObjectIdentification();
        poi.setParticipantObjectID(id);
        poi.setParticipantObjectIDTypeCode(idType);
        poi.setParticipantObjectName(name);
        poi.setParticipantObjectQuery(query);
        poi.setParticipantObjectTypeCode(type);
        poi.setParticipantObjectTypeCodeRole(role);
        poi.setParticipantObjectDataLifeCycle(lifeCycle);
        poi.setParticipantObjectSensitivity(sensitivity);
        poi.setParticipantObjectDescription(description);
        for (ParticipantObjectDetail detail : details)
            poi.getParticipantObjectDetail().add(detail);
        return poi;
    }
   
    public static ParticipantObjectDescription createParticipantObjectDescription(
            Boolean encrypted, Boolean anonymized) {
        ParticipantObjectDescription pod = new ParticipantObjectDescription();
        pod.setEncrypted(encrypted);
        pod.setAnonymized(anonymized);
        return pod;
    }

    public static ParticipantObjectDetail createParticipantObjectDetail(
            String type, byte[] value) {
        ParticipantObjectDetail detail = new ParticipantObjectDetail();
        detail.setType(type);
        detail.setValue(value);
        return detail;
    }

    public static MPPS createMPPS(String uid) {
        MPPS mpps = new MPPS();
        mpps.setUID(uid);
        return mpps;
    }

    public static SOPClass createSOPClass(String uid, Integer numI) {
        SOPClass sopClass = new SOPClass();
        sopClass.setUID(uid);
        sopClass.setNumberOfInstances(numI);
        return sopClass;
    }

    public static Instance createInstance(String uid) {
        Instance inst = new Instance();
        inst.setUID(uid);
        return inst;
    }

    public static ParticipantObjectContainsStudy
            createParticipantObjectContainsStudy(String uid) {
        ParticipantObjectContainsStudy study = new ParticipantObjectContainsStudy();
        study.setUID(uid);
        return study;
    }

    public static Accession createAccession(String accessionNumber) {
        Accession accession = new Accession();
        accession.setNumber(accessionNumber);
        return accession;
    }

    public static String alternativeUserIDForAETitle(String... aets) {
        int iMax = aets.length - 1;
        if (iMax == -1)
            return null;

        StringBuilder b = new StringBuilder();
        b.append("AETITLES=");
        for (int i = 0; ; i++) {
            b.append(aets[i]);
            if (i == iMax)
                return b.toString();
            b.append(';');
        }
    }

    public static void toXML(AuditMessage message, OutputStream os)
            throws IOException {
        toXML(message, os, false, "UTF-8", SCHEMA_URI);
    }

    public static void toXML(AuditMessage message, OutputStream os,
            boolean format) throws IOException {
        toXML(message, os, format, "UTF-8", SCHEMA_URI);
    }

    public static void toXML(AuditMessage message, OutputStream os,
            boolean format, String encoding) throws IOException {
        toXML(message, os, format, encoding, SCHEMA_URI);
    }

    public static void toXML(AuditMessage message, OutputStream os,
            boolean format, String encoding, String schemaURI)
            throws IOException {
        try {
            Marshaller m = jc().createMarshaller();
            if (format)
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if (schemaURI != null)
                m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,
                        schemaURI);
            if (encoding != null)
                m.setProperty(Marshaller.JAXB_ENCODING, encoding);
            m.marshal(of.createAuditMessage(message), os );
        } catch( JAXBException jbe ){
            if (jbe.getLinkedException() instanceof IOException)
                throw (IOException) jbe.getLinkedException();
            throw new IllegalStateException(jbe);
        }
    }

    public static String toXML(AuditMessage message)
            throws IOException {
        return toXML(message, false, "UTF-8", SCHEMA_URI);
    }

    public static String toXML(AuditMessage message,
            boolean format) throws IOException {
        return toXML(message, format, "UTF-8", SCHEMA_URI);
    }

    public static String toXML(AuditMessage message,
            boolean format, String encoding) throws IOException {
        return toXML(message, format, encoding, SCHEMA_URI);
    }

    public static String toXML(AuditMessage message, 
            boolean format, String encoding, String schemaURL)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(256);
        toXML(message, os, format, encoding, schemaURL);
        return os.toString(encoding);
    }
}
