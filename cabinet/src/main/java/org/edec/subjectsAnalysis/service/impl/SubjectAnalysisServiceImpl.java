package org.edec.subjectsAnalysis.service.impl;

import org.edec.subjectsAnalysis.manager.SubjectsAnalysisManager;
import org.edec.subjectsAnalysis.model.SubjectsAnalysisModel;
import org.edec.subjectsAnalysis.model.SubjectsInfoModel;
import org.edec.subjectsAnalysis.model.SubjectsRetakeCountModel;
import org.edec.subjectsAnalysis.service.SubjectsAnalysisService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubjectAnalysisServiceImpl implements SubjectsAnalysisService {

    SubjectsAnalysisManager manager = new SubjectsAnalysisManager();

    @Override
    public List<SubjectsAnalysisModel> getExamsSubjectsList(Long idSem, Integer course) {
        List<SubjectsAnalysisModel> listExam = new ArrayList<>();
        List<SubjectsAnalysisModel> examRatingList = manager.getExamsSubjects(idSem, course);
        List<SubjectsRetakeCountModel> examOneRetakeCounts = manager.getPartRetake(idSem, 2, 1);
        List<SubjectsRetakeCountModel> examMoreThanOneRetakeCounts = manager.getPartRetake(idSem, 3, 1);
        if (!examRatingList.isEmpty()) {
            for (SubjectsAnalysisModel model : examRatingList) {
                if (model.getIdChair() == null)
                    continue;
                model.setAvgRating(calcAvgRating(model));
                model.setPartFive(Math.round(model.getFiveCount() / (getExamSubjectStudentsCount(model)) * 100));
                model.setPartFour(Math.round(model.getFourCount() / (getExamSubjectStudentsCount(model)) * 100));
                model.setPartThree(Math.round(model.getThreeCount() / (getExamSubjectStudentsCount(model)) * 100));
                model.setPartTwo(Math.round((model.getTwoCount() + model.getAbsenceCount()) / (getExamSubjectStudentsCount(model)) * 100));
                balancePercentage(model);

                model.setOneRetake(getPassRetakeCounts(examOneRetakeCounts, model, true));
                model.setMoreThenOneRetake(getPassRetakeCounts(examMoreThanOneRetakeCounts, model, true));

                model.setModa(getModeRating(model));
                listExam.add(model);
            }
        }
        return listExam;
    }

    private double getExamSubjectStudentsCount(SubjectsAnalysisModel model) {
        return model.getFiveCount() + model.getFourCount() + model.getThreeCount() + model.getTwoCount() + model.getAbsenceCount();
    }

    private double calcAvgRating(SubjectsAnalysisModel model) {
        long result = Math.round((model.getFiveCount() * 5 + model.getFourCount() * 4 + model.getThreeCount() * 3 + model.getTwoCount() * 2 + model.getAbsenceCount() * 2)
                / getExamSubjectStudentsCount(model) * 100);

        return ((double) result) / 100;
    }

    private double getPassSubjectStudentsCount(SubjectsAnalysisModel model) {
        return model.getPassCount() + model.getNotPassCount() + model.getAbsenceCount() + model.getPassCountThree() + model.getPassCountFour() + model.getPassCountFive();
    }

    private long calcPassPart(SubjectsAnalysisModel model) {
        return Math.round((model.getPassCount() + model.getPassCountThree() + model.getPassCountFour() + model.getPassCountFive())
                / getPassSubjectStudentsCount(model) * 100);
    }

    private Double getPassRetakeCounts(List<SubjectsRetakeCountModel> subjectsCountsList, SubjectsAnalysisModel model, boolean isExam) {
        SubjectsRetakeCountModel oneRetake = subjectsCountsList
                .stream()
                .filter((r) -> r.getSubjectname().equals(model.getSubjectname()))
                .findAny()
                .orElse(null);
        double totalCount = isExam ? getExamSubjectStudentsCount(model) : getPassSubjectStudentsCount(model);
        return oneRetake == null ? 0 : (Double.valueOf(oneRetake.getCount()) / totalCount) * 100;
    }


    private void balancePercentage(SubjectsAnalysisModel model) {
        if (model.getPartFive() + model.getPartFour() + model.getPartThree() + model.getPartTwo() > 100) {
            if (model.getPartTwo() > 0) {
                model.setPartTwo(model.getPartTwo() - 1);
            } else if (model.getPartThree() > 0) {
                model.setPartThree(model.getPartThree() - 1);
            } else if (model.getPartFour() > 0) {
                model.setPartFour(model.getPartFour() - 1);
            } else {
                model.setPartFive(model.getPartFive() - 1);
            }
        } else if (model.getPartFive() + model.getPartFour() + model.getPartThree() + model.getPartTwo() < 100) {
            if (model.getPartTwo() > 0) {
                model.setPartTwo(model.getPartTwo() + 1);
            } else if (model.getPartThree() > 0) {
                model.setPartThree(model.getPartThree() + 1);
            } else if (model.getPartFour() > 0) {
                model.setPartFour(model.getPartFour() + 1);
            } else {
                model.setPartFive(model.getPartFive() + 1);
            }
        }
    }

    private Integer getModeRating(SubjectsAnalysisModel model) {
        int modestRating;
        Double modeCount = Math.max(model.getFiveCount(),
                Math.max(model.getFourCount(),
                        Math.max(model.getThreeCount(), model.getTwoCount() + model.getAbsenceCount())));
        if (modeCount.equals(model.getFiveCount())) {
            modestRating = 5;
        } else if (modeCount.equals(model.getFourCount())) {
            modestRating = 4;
        } else if (modeCount.equals(model.getThreeCount())) {
            modestRating = 3;
        } else {
            modestRating = 2;
        }
        return modestRating;
    }

    @Override
    public List<SubjectsAnalysisModel> getPassSubjectsList(Long idSem, Integer course) {
        List<SubjectsAnalysisModel> listPass = new ArrayList<>();
        List<SubjectsAnalysisModel> passRatingList = manager.getPassSubjects(idSem, course);
        List<SubjectsRetakeCountModel> passOneRetakeCounts = manager.getPartRetake(idSem, 2, 2);
        List<SubjectsRetakeCountModel> passMoreThanOneRetakeCounts = manager.getPartRetake(idSem, 3, 2);
        for (SubjectsAnalysisModel model : passRatingList) {
            if (model.getIdChair() == null)
                continue;
            model.setPartPass(calcPassPart(model));
            model.setPartNoPass(100 - model.getPartPass());
            model.setOneRetake(getPassRetakeCounts(passOneRetakeCounts, model, false));
            model.setMoreThenOneRetake(getPassRetakeCounts(passMoreThanOneRetakeCounts, model, false));

            listPass.add(model);
        }
        return listPass;
    }

    @Override
    public List<SubjectsInfoModel> getSubjectsInfo(String subjectname, Long idSem, Integer course, Integer foc, Integer idChair) {
        return manager.getTeacherAndGroup(subjectname, idSem, course, foc, idChair);
    }

    /**
     * Поиск стандартного отклонения
     * 0 - среднее
     * 1 - % пересдач
     * 2 - % троек
     * 3 - % пятёрок
     *
     * @param list
     * @return
     */
    @Override
    public double getStDev(List<SubjectsAnalysisModel> list, int type) {
        double mean = 0.0;
        double num = 0.0;
        double numi = 0.0;

        mean = getAvg(list, type);

        for (SubjectsAnalysisModel model : list) {
            if (type == 0) {
                if (model.getAvgRating() != null) numi = Math.pow((model.getAvgRating() - mean), 2);
            }
            if (type == 1) {
                if (model.getOneRetake() != null) numi = Math.pow((model.getOneRetake() - mean), 2);
            }
            if (type == 2) {
                numi = Math.pow((model.getPartThree() - mean), 2);
            }
            if (type == 3) {
                numi = Math.pow((model.getPartFive() - mean), 2);
            }

            num += numi;
        }

        return Math.sqrt(num / list.size());
    }

    /**
     * Поиск Среднего
     * 0 - среднее
     * 1 - % пересдач
     * 2 - % троек
     * 3 - % пятёрок
     *
     * @param list
     * @return
     */
    @Override
    public double getAvg(List<SubjectsAnalysisModel> list, int type) {
        double sum = 0.0;

        for (SubjectsAnalysisModel model : list) {
            if (type == 0) {
                if (model.getAvgRating() != null) sum += model.getAvgRating();
            }
            if (type == 1) {
                if (model.getOneRetake() != null) sum += model.getOneRetake();
            }
            if (type == 2) {
                sum += model.getPartThree();
            }
            if (type == 3) {
                sum += model.getPartFive();
            }
        }

        return sum / list.size();
    }

    /**
     * Поиск Верхнего квартиля
     * 0 - более 1 пересдачи
     * 1 - двоечники
     * 2 - зачет
     * 3 - незачет
     *
     * @param list
     * @return
     */
    public double getHQ(List<SubjectsAnalysisModel> list, int type) {
        double[] arr = {};

        for (SubjectsAnalysisModel model : list) {
            if (type == 0) {
                if (model.getMoreThenOneRetake() != null) {
                    arr = Arrays.copyOf(arr, arr.length + 1);
                    arr[arr.length - 1] = model.getMoreThenOneRetake();
                }
            }
            if (type == 1) {
                arr = Arrays.copyOf(arr, arr.length + 1);
                arr[arr.length - 1] = model.getPartTwo();
            }
            if (type == 2) {
                arr = Arrays.copyOf(arr, arr.length + 1);
                arr[arr.length - 1] = model.getPartPass();
            }
            if (type == 3) {
                arr = Arrays.copyOf(arr, arr.length + 1);
                arr[arr.length - 1] = model.getPartNoPass();
            }
        }

        return calcHQ(arr);
    }

    public double calcLQ(double[] arr) {
        Arrays.sort(arr);

        int length = arr.length;
        double LQ = 0;

        if (length < 4) {
            LQ = 0;
        } else if (length % 2 != 0) {
            LQ = (arr[(length / 4) - 1] + arr[length / 4]) / 2;
        } else {
            LQ = arr[(length / 4) - 1];
        }

        return LQ;
    }

//    public double getMedian(List<SubjectsAnalysisModel> list, int type){
//
//    }

    public double calcHQ(double[] arr) {
        Arrays.sort(arr);

        int length = arr.length;
        double HQ = 0;

        if (length < 4) {
            HQ = 0;
        } else if (length % 2 != 0) {
            HQ = (arr[(length * 3 / 4) - 1] + arr[length * 3 / 4]) / 2;
        } else {
            HQ = arr[(length * 3 / 4) - 1];
        }

        return HQ;
    }
}
