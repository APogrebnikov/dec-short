package org.edec.commission.report.model;

/**
 * Created by apple on 20.10.17.
 */
public class SemesterRatingModel implements Comparable<SemesterRatingModel> {
    private Integer startYear, endYear, season;

    public int getStartYear () {
        return startYear;
    }

    public void setStartYear (int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear () {
        return endYear;
    }

    public void setEndYear (int endYear) {
        this.endYear = endYear;
    }

    public int getSeason () {
        return season;
    }

    public void setSeason (int season) {
        this.season = season;
    }

    @Override
    public int compareTo (SemesterRatingModel o) {
        if (startYear.intValue() != o.getStartYear()) {
            return startYear.compareTo(o.getStartYear());
        }

        if (season.intValue() != o.getSeason()) {
            return season.compareTo(o.getSeason());
        }

        return 0;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SemesterRatingModel)) {
            return false;
        }

        SemesterRatingModel ratingModel = (SemesterRatingModel) obj;

        return this.startYear.intValue() == ratingModel.getStartYear() && this.season.intValue() == ratingModel.getSeason();
    }

    @Override
    public int hashCode () {
        return Integer.parseInt(startYear.toString() + endYear.toString() + season.toString());
    }
}
