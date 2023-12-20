package org.edec.successful.service.impl;

import org.edec.successful.ReportMarksEnum;
import org.edec.successful.model.MarksStatModel;
import org.edec.successful.model.Measure;
import org.edec.successful.model.RatingModel;
import org.edec.successful.model.ReportStatModel;
import org.edec.successful.model.StudentReportModel;
import org.edec.successful.model.SuccessfulTreeModel;
import org.edec.utility.constants.RatingConst;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SuccessfulReportDataService {
    public ReportStatModel getStatData(List<Measure> mainFilter, List<RatingModel> ratings, List<String> values, String curValue, Measure curMeasure) {
        List<RatingModel> filteredRatings = ratings;

        for(int i = 0; i < mainFilter.size(); i++) {
            filteredRatings = filterRatings(mainFilter.get(i), values.get(i), filteredRatings);
        }

        if(curMeasure != null && curValue != null) {
            filteredRatings = filterRatings(curMeasure, curValue, filteredRatings);
        }

        Set<String> setRecordBooks = getSetRecordBooksFromRatings(filteredRatings);

        ReportStatModel reportStatModel = new ReportStatModel();

        for(String recordbook : setRecordBooks) {
            List<RatingModel> studentRatings = filteredRatings.stream().filter(el -> el.getRecordbook().equals(recordbook)).collect(Collectors.toList());
            StudentReportModel studentReportModel = createStudentModel(studentRatings);

            reportStatModel.getAllStudents().add(studentReportModel);

            if(studentReportModel.getMarksType() == null) {
                reportStatModel.getSuccessfulStudents().add(studentReportModel);
                continue;
            }

            switch (studentReportModel.getMarksType()) {
                case ALL_FIVE:
                    reportStatModel.getStudentsWithOnlyFiveMarks().add(studentReportModel);
                    reportStatModel.getSuccessfulStudents().add(studentReportModel);
                    break;
                case FIVE_AND_FOUR:
                    reportStatModel.getStudentsWithFiveAndFourMarks().add(studentReportModel);
                    reportStatModel.getSuccessfulStudents().add(studentReportModel);
                    break;
                case ALL_FOUR:
                    reportStatModel.getStudentsWithOnlyFourMarks().add(studentReportModel);
                    reportStatModel.getSuccessfulStudents().add(studentReportModel);
                    break;
                case THREE_FOUR_FIVE:
                    reportStatModel.getStudentsWithFiveFourThreeMarks().add(studentReportModel);
                    reportStatModel.getSuccessfulStudents().add(studentReportModel);
                    break;
                case ALL_THREE:
                    reportStatModel.getStudentsWithOnlyThreeMarks().add(studentReportModel);
                    reportStatModel.getSuccessfulStudents().add(studentReportModel);
                    break;
                case TWO_THREE:
                    reportStatModel.getStudentsWithTwoThreeMarks().add(studentReportModel);
                    break;
                case DEBTOR:
                case FULL_DEBTOR:
                    if(studentReportModel.getMarksType() == ReportMarksEnum.FULL_DEBTOR) {
                        reportStatModel.getStudentsWithAllDebts().add(studentReportModel);
                    }

                    switch (studentReportModel.getCountDebts()) {
                        case 0:
                            break;
                        case 1:
                            reportStatModel.getStudentsWithOneDebt().add(studentReportModel);
                            reportStatModel.getStudentsWithDebts().add(studentReportModel);
                            break;
                        case 2:
                            reportStatModel.getStudentsWithTwoDebts().add(studentReportModel);
                            reportStatModel.getStudentsWithDebts().add(studentReportModel);
                            break;
                        case 3:
                        case 4:
                            reportStatModel.getStudentsWithThreeOrFourDebts().add(studentReportModel);
                            reportStatModel.getStudentsWithDebts().add(studentReportModel);
                            break;
                        default: //  5 и больше долгов
                            reportStatModel.getStudentsWithMoreThanFiveDebts().add(studentReportModel);
                            reportStatModel.getStudentsWithDebts().add(studentReportModel);
                    }

                    break;
            }
        }

        return reportStatModel;
    }

    private Set<String> getSetRecordBooksFromRatings(List<RatingModel> ratingModels) {
        Set<String> recordBooks = new HashSet<>();

        for(RatingModel model : ratingModels) {
            recordBooks.add(model.getRecordbook());
        }

        return recordBooks;
    }

    private StudentReportModel createStudentModel(List<RatingModel> ratingModels) {
        StudentReportModel studentReportModel = new StudentReportModel();
        studentReportModel.setFio(ratingModels.get(0).getFio());
        studentReportModel.setRecordbook(ratingModels.get(0).getRecordbook());

        MarksStatModel statModel = calculateMarks(ratingModels);

        studentReportModel.setCountDebts(statModel.getCountDebts());

        if(statModel.getCountFiveMarks() == 0 & statModel.getCountFourMarks() == 0 & statModel.getCountThreeMarks() == 0 & statModel.getCountDebts() == 0) {
            // DO NOTHING, only passes
        } else if(statModel.getCountFiveMarks() > 0 & statModel.getCountFourMarks() == 0 & statModel.getCountThreeMarks() == 0 & statModel.getCountDebts() == 0) {
            studentReportModel.setMarksType(ReportMarksEnum.ALL_FIVE);
        } else if(statModel.getCountFiveMarks() == 0 & statModel.getCountFourMarks() > 0 & statModel.getCountThreeMarks() == 0 & statModel.getCountDebts() == 0) {
            studentReportModel.setMarksType(ReportMarksEnum.ALL_FOUR);
        } else if(statModel.getCountFiveMarks() == 0 & statModel.getCountFourMarks() == 0 & statModel.getCountThreeMarks() > 0 & statModel.getCountDebts() == 0) {
            studentReportModel.setMarksType(ReportMarksEnum.ALL_THREE);
        } else if(statModel.getCountFiveMarks() > 0 & statModel.getCountFourMarks() > 0 & statModel.getCountThreeMarks() == 0 & statModel.getCountDebts() == 0) {
            studentReportModel.setMarksType(ReportMarksEnum.FIVE_AND_FOUR);
        } else if(statModel.getCountFiveMarks() >= 0 & statModel.getCountFourMarks() >= 0 & statModel.getCountThreeMarks() >= 0 & statModel.getCountDebts() == 0) {
            studentReportModel.setMarksType(ReportMarksEnum.THREE_FOUR_FIVE);
        } else if(statModel.getCountFiveMarks() == 0 & statModel.getCountFourMarks() == 0 & statModel.getCountThreeMarks() > 0 & statModel.getCountTwoMarks() >= 0) {
            studentReportModel.setMarksType(ReportMarksEnum.TWO_THREE);
        }

        if(statModel.getCountDebts() > 0) {
            studentReportModel.setMarksType(ReportMarksEnum.DEBTOR);
        }

        if(statModel.getCountDebts() == ratingModels.size()) {
            studentReportModel.setMarksType(ReportMarksEnum.FULL_DEBTOR);
        }

        return studentReportModel;
    }

    private MarksStatModel calculateMarks(List<RatingModel> ratingModels) {
        MarksStatModel marksStatModel = new MarksStatModel();

        for(RatingModel r : ratingModels) {
            if(r.getRating() == null || r.getRating() == 0 || r.getRating() == RatingConst.NOT_LEARNED.getRating()) {
                continue;
            }

            if(r.getRating() == RatingConst.EXCELLENT.getRating()) {
                marksStatModel.setCountFiveMarks(marksStatModel.getCountFiveMarks() + 1);
            }

            if(r.getRating() == RatingConst.GOOD.getRating()) {
                marksStatModel.setCountFourMarks(marksStatModel.getCountFourMarks() + 1);
            }

            if(r.getRating() == RatingConst.SATISFACTORILY.getRating()) {
                marksStatModel.setCountThreeMarks(marksStatModel.getCountThreeMarks() + 1);
            }

            if(r.getRating() == RatingConst.PASS.getRating()) {
                marksStatModel.setCountPassMarks(marksStatModel.getCountPassMarks() + 1);
            }

            if(r.getRating() == RatingConst.NOT_PASS.getRating()) {
                marksStatModel.setCountUnpassMarks(marksStatModel.getCountUnpassMarks() + 1);
                marksStatModel.setCountDebts(marksStatModel.getCountDebts() + 1);
            }

            if(r.getRating() == RatingConst.UNSATISFACTORILY.getRating()) {
                marksStatModel.setCountTwoMarks(marksStatModel.getCountTwoMarks() + 1);
                marksStatModel.setCountDebts(marksStatModel.getCountDebts() + 1);
            }

            if(r.getRating() == RatingConst.FAILED_TO_APPEAR.getRating()) {
                marksStatModel.setCountMissToAppearMarks(marksStatModel.getCountMissToAppearMarks() + 1);
                marksStatModel.setCountDebts(marksStatModel.getCountDebts() + 1);
            }
        }

        return marksStatModel;
    }

    private List<RatingModel> filterRatings(Measure curMeasure, String curValue, List<RatingModel> filteredRatings) {
        switch (curMeasure) {
            case TEACH_CHAIR:
                return filteredRatings.stream().filter(el -> el.getTChairFulltitle().equals(curValue)).collect(Collectors.toList());
            case OUT_CHAIR:
                return filteredRatings.stream().filter(el -> el.getEChairFulltitle().equals(curValue)).collect(Collectors.toList());
            case COURSE:
                return filteredRatings.stream().filter(el -> el.getCourse().toString().equals(curValue)).collect(Collectors.toList());
            case GROUP:
                return filteredRatings.stream().filter(el -> el.getGroupname().equals(curValue)).collect(Collectors.toList());
            case SUBJECT:
                return filteredRatings.stream().filter(el -> el.getSubjectName().equals(curValue)).collect(Collectors.toList());
            default:
                return filteredRatings;
        }
    }

    public SuccessfulTreeModel createTreeModel(List<RatingModel> ratings, List<Measure> groupings)
            throws IllegalArgumentException, NullPointerException{
        if(ratings == null || groupings == null) {
            throw new NullPointerException("Один или несколько из передаваемых аргументов null");
        }

        if(groupings.size() == 0) {
            throw new IllegalArgumentException("Список групировок не может быть пустым");
        }

        Comparator<RatingModel> comparator = getComparatorByGrouping(groupings.get(0), null);


        for(int i = 1; i < groupings.size(); i++) {
            comparator = getComparatorByGrouping(groupings.get(i), comparator);
        }

        ratings.sort(comparator);

        SuccessfulTreeModel root = new SuccessfulTreeModel(null);

        buildLayer(root, groupings, ratings);

        return root;
    }

    private void buildLayer(SuccessfulTreeModel root, List<Measure> groupings, List<RatingModel> ratings) {
        if(groupings.size() == 0) {
            return;
        }

        List<String> values = getValuesForLayer(groupings.get(0), ratings);
        for(String value : values) {
            SuccessfulTreeModel child = new SuccessfulTreeModel(root);
            child.setValue(value);

            root.addChild(child);


            if(groupings.size() != 1) {
                buildLayer(child, groupings.subList(1, groupings.size()), filterByGroupingAndValue(ratings, value, groupings.get(0)));
            } else {
                List<RatingModel> list = filterByGroupingAndValue(ratings, value, groupings.get(0));
                fillStudentsLayer(list, child);
            }
        }
    }

    private void fillStudentsLayer(List<RatingModel> ratings, SuccessfulTreeModel child) {
        Set<String> recordBooks = getSetRecordBooksFromRatings(ratings);

        for(String recordBook : recordBooks) {
            buildStudentNode(
                    ratings.stream().filter(el -> el.getRecordbook() != null && el.getRecordbook().equals(recordBook))
                           .collect(Collectors.toList()), child
            );
        }
    }

    private void buildStudentNode(List<RatingModel> ratingModels, SuccessfulTreeModel parent) {
        StudentReportModel studentReportModel = createStudentModel(ratingModels);

        SuccessfulTreeModel successfulTreeModel = new SuccessfulTreeModel(parent);
        successfulTreeModel.setValue(studentReportModel.getFio());
        successfulTreeModel.setStudentReportModel(studentReportModel);
        parent.addChild(successfulTreeModel);
    }

    public String getHeaderValueForLayer(Measure grouping) {
        switch (grouping) {
            case GROUP:
                return "Группа";
            case COURSE:
                return "Курс";
            case OUT_CHAIR:
                return "Вып. кафедра";
            case TEACH_CHAIR:
                return "Преп. кафедра";
            case SUBJECT:
                return "Предмет";
            default:
                return null;
        }
    }

    private List<String> getValuesForLayer(Measure grouping, List<RatingModel> ratings) {
        switch (grouping) {
            case GROUP:
                return ratings.stream().map(RatingModel::getGroupname).distinct().collect(Collectors.toList());
            case COURSE:
                return ratings.stream().map(el -> el.getCourse().toString()).distinct().collect(Collectors.toList());
            case OUT_CHAIR:
                return ratings.stream().map(RatingModel::getEChairFulltitle).distinct().collect(Collectors.toList());
            case TEACH_CHAIR:
                return ratings.stream().map(RatingModel::getTChairFulltitle).distinct().collect(Collectors.toList());
            case SUBJECT:
                return ratings.stream().map(RatingModel::getSubjectName).distinct().collect(Collectors.toList());
            default:
                return null;
        }
    }

    private List<RatingModel> filterByGroupingAndValue(List<RatingModel> ratings, String value, Measure grouping) {
        switch (grouping) {
            case GROUP:
                return ratings.stream().filter(el -> el.getGroupname().equals(value)).collect(Collectors.toList());
            case COURSE:
                return ratings.stream().filter(el -> el.getCourse().toString().equals(value)).collect(Collectors.toList());
            case OUT_CHAIR:
                return ratings.stream().filter(el -> el.getEChairFulltitle().equals(value)).collect(Collectors.toList());
            case TEACH_CHAIR:
                return ratings.stream().filter(el -> el.getTChairFulltitle().equals(value)).collect(Collectors.toList());
            case SUBJECT:
                return ratings.stream().filter(el -> el.getSubjectName().equals(value)).collect(Collectors.toList());
            default:
                return null;
        }
    }

    private Comparator<RatingModel> getComparatorByGrouping(Measure grouping, Comparator<RatingModel> comparator) {
        if(comparator == null) {
            switch (grouping) {
                case GROUP:
                    return Comparator.comparing(RatingModel::getGroupname);
                case COURSE:
                    return Comparator.comparing(RatingModel::getCourse);
                case OUT_CHAIR:
                    return Comparator.comparing(RatingModel::getEChairFulltitle);
                case TEACH_CHAIR:
                    return Comparator.comparing(RatingModel::getTChairFulltitle);
                case SUBJECT:
                    return Comparator.comparing(RatingModel::getSubjectName);
                default:
                    return null;
            }
        } else {
            switch (grouping) {
                case GROUP:
                    return comparator.thenComparing(RatingModel::getGroupname);
                case COURSE:
                    return comparator.thenComparing(RatingModel::getCourse);
                case OUT_CHAIR:
                    return comparator.thenComparing(RatingModel::getEChairFulltitle);
                case TEACH_CHAIR:
                    return comparator.thenComparing(RatingModel::getTChairFulltitle);
                case SUBJECT:
                    return comparator.thenComparing(RatingModel::getSubjectName);
                default:
                    return null;
            }
        }
    }
}
