package org.sakaiproject.cmprovider;

import java.util.List;

import org.sakaiproject.cmprovider.api.CourseOfferingEntityProvider;
import org.sakaiproject.cmprovider.api.data.CourseOfferingData;
import org.sakaiproject.cmprovider.api.data.MembershipData;
import org.sakaiproject.cmprovider.api.data.util.DateUtils;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.entitybroker.EntityReference;
import org.sakaiproject.entitybroker.entityprovider.search.Search;

/**
 * Provides a REST API for working with course offerings.
 * @see CourseOffering
 *
 * @author Christopher Schauer
 */
public class CourseOfferingEntityProviderImpl extends AbstractContainerEntityProvider implements CourseOfferingEntityProvider {
  public String getEntityPrefix() {
    return ENTITY_PREFIX;
  }

  public Object getSampleEntity() {
    return new CourseOfferingData();
  }

  @Override
  public boolean entityExists(String id) {
    return cmService.isCourseOfferingDefined(id);
  }

  /**
   * TODO: look into implementing search for course offerings
   */
  public List getEntities(EntityReference ref, Search search) {
    validateUser();
    return null;
  }

  /**
   * Create a new course offering. Wraps CourseManagementAdministration.createCourseOffering.
   * @see CourseManagementAdministration#createCourseOffering
   * @see CourseOfferingData
   */
  public void create(Object entity) {
    CourseOfferingData data = (CourseOfferingData) entity;
    cmAdmin.createCourseOffering(
      data.eid,
      data.title,
      data.description,
      data.status,
      data.academicSession,
      data.canonicalCourse,
      DateUtils.stringToDate(data.startDate),
      DateUtils.stringToDate(data.endDate)
    );
  }

  /**
   * Update a course offering. Wraps CourseManagementAdministration.updateCourseOffering.
   * @see CourseManagementAdministration#updateCourseOffering.
   * @see CourseOfferingData
   */
  public void update(Object entity) {
    CourseOfferingData data = (CourseOfferingData) entity;
    CourseOffering updated = cmService.getCourseOffering(data.eid);
    updated.setTitle(data.title);
    updated.setDescription(data.description);
    updated.setStatus(data.status);
    updated.setStartDate(DateUtils.stringToDate(data.startDate));
    updated.setEndDate(DateUtils.stringToDate(data.endDate));

    if (updated.getAcademicSession() == null || updated.getAcademicSession().getEid() != data.academicSession) {
      AcademicSession newSession = cmService.getAcademicSession(data.academicSession);
      updated.setAcademicSession(newSession);
    }

    // TODO: There's no method to set canonical course for CourseOffering. Not sure if this is something
    // that we'll want to implement. It does make sense that you wouldn't ever want to change canonical course
    // since you're basically talking about a completely new course offering at that point.
    cmAdmin.updateCourseOffering(updated);
  }

  /**
   * Get a course offering. Wraps CourseManagementService.getCourseOffering.
   * @see CourseManagementService#getCourseOffering
   */
  public Object get(String eid) {
    CourseOffering course = cmService.getCourseOffering(eid);
    return new CourseOfferingData(course);
  }

  /**
   * Delete a course offering. Wraps CourseManagementAdministration.removeCourseOffering
   * @see CourseManagementAdministration#removeCourseOffering
   */
  public void delete(String eid) {
    cmAdmin.removeCourseOffering(eid);
  }

  public void addOrUpdateMembership(MembershipData data) {
    cmAdmin.addOrUpdateCourseOfferingMembership(
      data.userId,
      data.role,
      data.memberContainer,
      data.status
    );
  }

  public void removeMembership(String userId, String containerId) {
    cmAdmin.removeCourseOfferingMembership(userId, containerId);
  }
}
