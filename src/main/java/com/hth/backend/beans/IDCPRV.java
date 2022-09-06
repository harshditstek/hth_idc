package com.hth.backend.beans;

import java.awt.Image;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import com.hth.backend.iSeries;
import com.hth.id_card.HTH_IDC;
import com.hth.id_card.user_interface.printer.DataSingleton;
import com.hth.util.IDCard;
import com.hth.util.IDCardDownload;
import com.hth.util.IDMask;

public class IDCPRV {

    public static List<String[]> get() {
        String alias = "QTEMP.IDCPRV";
        String file = "DFLIB.IDCPRV(TL1)";
        String sql;
        List<String[]> resultList;
        sql = "SELECT * FROM QTEMP.IDCPRV limit 10";
        System.out.println(sql);
        resultList = iSeries.executeSQLByAlias(sql, alias, file);

        return resultList;
    }

    public static IDCard[] generateIDCARD(String[] grpList, String[] ssnList, String device) {
        System.out.println("dev:"+device);

        String member = HTH_IDC.member;
        IDCard[] idCardList = new IDCard[0];

        String[] alias = {"QTEMP.IDCPRV", "QTEMP.INSURE", "QTEMP.CODCOV", "QTEMP.INSHST"};
        String[] file = {"DFLIB.IDCPRV(" + member + ")", "DFLIB.INSURE(" + member + ")", "DFLIB.CODCOV(" + member + ")", "DFLIB.INSHST(" + member + ")"};
        String sql;
        List<String[]> resultList;

        StringBuilder whereClause = new StringBuilder("(");
        StringBuilder ssnClause = new StringBuilder("(");
        whereClause.append("(a.PGRP='").append(grpList[0]).append("' AND a.PSSN='").append(ssnList[0]).append("')");
        for (int idx = 1; idx < grpList.length; idx++) {
            whereClause.append(" OR (a.PGRP='").append(grpList[idx]).append("' AND a.PSSN='").append(ssnList[idx]).append("')");
        }
        whereClause.append(")");
        sql = "SELECT a.IGRP, a.IDIV, a.ICRD#, PLARF, a.PLARB, " // {idx 0 ~ 4}
                + "a.PFL01, a.PFL02, a.PFL03, a.PFL04, a.PFL05, a.PFL06, a.PFL07, a.PFL08, a.PFL09, a.PFL10, a.PFL11, a.PFL12, a.PFL13, a.PFL14, a.PFL15, a.PFL16, a.PFL17, a.PFL18, " // {idx 5 ~ 22}
                + "a.PBL01, a.PBL02, a.PBL03, a.PBL04, a.PBL05, a.PBL06, a.PBL07, a.PBL08, a.PBL09, a.PBL10, a.PBL11, a.PBL12, a.PBL13, a.PBL14, a.PBL15, a.PBL16, a.PBL17, a.PBL18, " // {idx 23 ~ 40}
                + "a.PFM01, a.PFM02, a.PFM03, a.PFM04, a.PFM05, a.PFM06, a.PFM07, a.PFM08, a.PFM09, a.PFM10, a.PFM11, a.PFM12, a.PFM13, a.PFM14, a.PFM15, a.PFM16, a.PFM17, a.PFM18, " // {idx 41 ~ 58}
                + "a.PBM01, a.PBM02, a.PBM03, a.PBM04, a.PBM05, a.PBM06, a.PBM07, a.PBM08, a.PBM09, a.PBM10, a.PBM11, a.PBM12, a.PBM13, a.PBM14, a.PBM15, a.PBM16, a.PBM17, a.PBM18, " // {idx 59 ~ 76}
                + "a.PFLG1, a.PFLG2, a.PFLG3, a.PFLG4, a.PFLG5, a.PFLG6, a.PFLG7, a.PFLG8, a.PFLG9, " // {idx 77 ~ end}
                + "a.PBLG1, a.PBLG2, a.PBLG3, a.PBLG4, a.PBLG5, a.PBLG6, a.PBLG7, a.PBLG8, a.PBLG9, a.PGRa.P, a.PSSN, a.PFNAM, a.PLNAM, a.ICRD#, a.PBL17,a.PDATE, a.PTIME, "
                + "b.IINE01, b.IINE02, b.IINE03, b.IINE04, b.IINE05, b.IINE06, b.IINE07, b.IINE08, b.IINE09, b.IINE10,"
                + "b.IINE11, b.IINE12, b.IINE13,b.IINE14, b.IINE15, b.IINE16, b.IINE17, b.IINE18, b.IINE19, b.IINE20,"
                + "b.IINE21, b.IINE22, b.IINE23, b.IINE24, b.IINE25, b.IINE26, b.IINE27, b.IINE28, b.IINE29, b.IINE30,"
                + "b.IINE31, b.IINE32, b.IINE33, b.IINE34, b.IINE35, b.IINE36, b.IINE37, b.IINE38, b.IINE39, b.IINE40,"
                + "b.IINE41, b.IINE42, b.IINE43, b.IINE44, b.IINE45, b.IINE46, b.IINE47, b.IINE48, b.IINE49, b.IINE50, b.IOCV1,"
                + "c.TDES, "
                + "d.HPEFF, d.HPTER "
                + "FROM QTEMP.IDCPRV as a "
                + "INNER JOIN QTEMP.INSURE as b ON b.ISSN = a.PSSN "
                + "INNER JOIN QTEMP.CODCOV as c ON c.TCODE = b.IOCV1 "
                + "INNER JOIN QTEMP.INSHST as d ON d.HSSN = a.PSSN "
                + "WHERE a.PDEV='" + device + "' AND " + whereClause
                + " ORDER BY a.PGRP, a.PDIV, a.PLNAM, a.PFNAM";
        System.out.println(sql);
        resultList = iSeries.executeSQLByAlias(sql, alias, file);
        DataSingleton.singleton().setInsureList(resultList);
        if (!resultList.isEmpty()) {
            idCardList = new IDCard[resultList.size()];
            for (int rsIdx = 0; rsIdx < resultList.size(); rsIdx++) {
                String[] idCardInfo = resultList.get(rsIdx);
                final String iGrp = idCardInfo[0];
                final String iDiv = idCardInfo[1];
                final String iCrdNum = idCardInfo[2];
                String frontLarge = idCardInfo[3].trim();
                String backLarge = idCardInfo[4].trim();
                String[] frontTexts = new String[18];
                String[] backTexts = new String[18];
                String[] frontTextModifiers = new String[18];
                String[] backTextModifiers = new String[18];
                String[] logos = new String[18];
                List<IDMask> frontMask = new ArrayList<>();
                List<IDMask> backMask = new ArrayList<>();

                final IDMask[][] idMaskList = new IDMask[1][1];
                Thread maskLoader = new Thread(new Runnable() {
                    public void run() {
                        idMaskList[0] = IDCMASK.getIdCardMask(iGrp, iDiv, iCrdNum);
                    }
                });
                maskLoader.start();

                for (int idx = 5; idx < idCardInfo.length; idx++) {
                    // Front texts.
                    if (idx <= 22) {
                        frontTexts[idx - 5] = idCardInfo[idx].trim();
                    }

                    // Back texts.
                    if (idx >= 23 && idx <= 40) {
                        backTexts[idx - 23] = idCardInfo[idx].trim();
                    }

                    // Front text modifiers.
                    if (idx >= 41 && idx <= 58) {
                        frontTextModifiers[idx - 41] = idCardInfo[idx].trim();
                    }

                    // Back text modifiers.
                    if (idx >= 59 && idx <= 76) {
                        backTextModifiers[idx - 59] += "," + idCardInfo[idx].trim();
                    }

                    // Logos
                    if (idx >= 77 && idx <= 94) {
                        if (!idCardInfo[idx].trim().equals("")) {
                            logos[idx - 77] = "/MobileApp/" + member + "/Logos/" + idCardInfo[idx].trim();
                        } else {
                            logos[idx - 77] = idCardInfo[idx].trim();
                        }
                    }
                }

                try {
                    maskLoader.join();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }

                for (IDMask mask : idMaskList[0]) {
                    if (mask.getFace().equals("F")) {
                        frontMask.add(mask);
                    } else {
                        backMask.add(mask);
                    }
                }

                IDCard idCard = new IDCard(frontTexts, backTexts, frontTextModifiers, backTextModifiers, frontMask.toArray(new IDMask[0]), backMask.toArray(new IDMask[0]), frontLarge, backLarge, "");
                Thread newTask = new Thread(new DownloadLogoTask(idCard, logos));
                newTask.start();

                idCardList[rsIdx] = idCard;
            }
        }

        clearIDCARD(grpList, ssnList, device);

        return idCardList;
    }

    private static void clearIDCARD(String[] grpList, String[] ssnList, String device) {
        String alias = "QTEMP.IDCPRV";
        String file = "DFLIB.IDCPRV(" + HTH_IDC.member + ")";
        String sql;

        StringBuilder whereClause = new StringBuilder("(");
        whereClause.append("(PGRP='").append(grpList[0]).append("' AND PSSN='").append(ssnList[0]).append("')");
        for (int idx = 1; idx < grpList.length; idx++) {
            whereClause.append(" OR (PGRP='").append(grpList[idx]).append("' AND PSSN='").append(ssnList[idx]).append("')");
        }
        whereClause.append(")");

        sql = "DELETE FROM " + alias + " WHERE PDEV='" + device + "' AND " + whereClause;
        iSeries.executeSQLByAlias(sql, alias, file);
    }

    private static class DownloadLogoTask implements Runnable {
        IDCard idCard;
        String[] logoPaths;

        DownloadLogoTask(IDCard idCard, String[] logoPaths) {
            this.idCard = idCard;
            this.logoPaths = logoPaths;
        }

        @Override
        public void run() {
            Image[] logos = iSeries.downloadImages(logoPaths);
            Image[] frontLogos = new Image[9];
            Image[] backLogos = new Image[9];

            for (int idx = 0; idx < logos.length; idx++) {
                if (idx < 9) {
                    frontLogos[idx] = logos[idx];
                } else {
                    backLogos[idx - 9] = logos[idx];
                }
            }

            idCard.setFrontLogo(frontLogos);
            idCard.setBackLogo(backLogos);
            idCard.changeReadyState(true);
        }
    }
}
