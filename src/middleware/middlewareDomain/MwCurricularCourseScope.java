// Generated by impart OJB Generator
// www.impart.ch matthias.roth@impart.ch
// created at 30 Sep 2003 12:17:16 GMT

package middleware.middlewareDomain;

public class MwCurricularCourseScope {

	private Integer branchcode;
	private String coursecode;
	private Integer coursetype;
	private Double credits;
	private Integer curricularsemester;
	private Integer curricularyear;
	private Integer degreecode;
	private Integer executionyear;
	private Double labhours;
	private String orientation;
	private Double praticahours;
	private Double theoprathours;
	private Double theoreticalhours;
	private Integer idInternal;

	public MwCurricularCourseScope() {
	}


	public String toString(){
		return  " [courseCode] " + coursecode + " [degreeCode] " + degreecode+ " [branchCode] " + branchcode;

	  }




	/**
	 * @return
	 */
	public Integer getBranchcode() {
		return branchcode;
	}

	/**
	 * @param branchcode
	 */
	public void setBranchcode(Integer branchcode) {
		this.branchcode = branchcode;
	}

	/**
	 * @return
	 */
	public String getCoursecode() {
		return coursecode;
	}

	/**
	 * @param coursecode
	 */
	public void setCoursecode(String coursecode) {
		this.coursecode = coursecode;
	}

	/**
	 * @return
	 */
	public Integer getCoursetype() {
		return coursetype;
	}

	/**
	 * @param coursetype
	 */
	public void setCoursetype(Integer coursetype) {
		this.coursetype = coursetype;
	}

	/**
	 * @return
	 */
	public Double getCredits() {
		return credits;
	}

	/**
	 * @param credits
	 */
	public void setCredits(Double credits) {
		this.credits = credits;
	}

	/**
	 * @return
	 */
	public Integer getCurricularsemester() {
		return curricularsemester;
	}

	/**
	 * @param curricularsemester
	 */
	public void setCurricularsemester(Integer curricularsemester) {
		this.curricularsemester = curricularsemester;
	}

	/**
	 * @return
	 */
	public Integer getCurricularyear() {
		return curricularyear;
	}

	/**
	 * @param curricularyear
	 */
	public void setCurricularyear(Integer curricularyear) {
		this.curricularyear = curricularyear;
	}

	/**
	 * @return
	 */
	public Integer getDegreecode() {
		return degreecode;
	}

	/**
	 * @param degreecode
	 */
	public void setDegreecode(Integer degreecode) {
		this.degreecode = degreecode;
	}

	/**
	 * @return
	 */
	public Integer getExecutionyear() {
		return executionyear;
	}

	/**
	 * @param executionyear
	 */
	public void setExecutionyear(Integer executionyear) {
		this.executionyear = executionyear;
	}

	/**
	 * @return
	 */
	public Integer getIdInternal() {
		return idInternal;
	}

	/**
	 * @param idInternal
	 */
	public void setIdInternal(Integer idInternal) {
		this.idInternal = idInternal;
	}

	/**
	 * @return
	 */
	public Double getLabhours() {
		return labhours;
	}

	/**
	 * @param labhours
	 */
	public void setLabhours(Double labhours) {
		this.labhours = labhours;
	}

	/**
	 * @return
	 */
	public String getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return
	 */
	public Double getPraticahours() {
		return praticahours;
	}

	/**
	 * @param praticahours
	 */
	public void setPraticahours(Double praticahours) {
		this.praticahours = praticahours;
	}

	/**
	 * @return
	 */
	public Double getTheoprathours() {
		return theoprathours;
	}

	/**
	 * @param theoprathours
	 */
	public void setTheoprathours(Double theoprathours) {
		this.theoprathours = theoprathours;
	}

	/**
	 * @return
	 */
	public Double getTheoreticalhours() {
		return theoreticalhours;
	}

	/**
	 * @param theoreticalhours
	 */
	public void setTheoreticalhours(Double theoreticalhours) {
		this.theoreticalhours = theoreticalhours;
	}

}