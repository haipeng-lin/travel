package com.wen.shuzhi.monitorSystem.controller;

import com.wen.shuzhi.monitorSystem.entity.TripleSmoothingEntity;
import com.wen.shuzhi.monitorSystem.service.WaterService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.DecimalFormat;
import java.util.*;

@Controller
@Slf4j
public class  TripleSmoothController {

    @Autowired
    private WaterService waterService;

    @Autowired
    WaterController waterController;

    @PostMapping("/tripleExponentialSmoothingMethod")
    @ApiOperation("三次指数平滑法")
    public Double tripleExponentialSmoothingMethod(@RequestBody TripleSmoothingEntity tripleSmoothingEntity,
                                                   Model model){
        /** 三次指数平滑公式：
         * 注：3^2 代表3的平方,[1-a]^2 代表 1-a 的平方
         *  St3 = a * St2(二次平滑结果) + (1-a) * St3-1
         *  At = 3 * St1 - 3 * St2 + St3
         *  Bt = a / 2*[(1-a)^2] * [(6-5a)*St1 - 2*(5-4a)*St2 + (4-3a)*St3]
         *  Ct = a^2 / 2*[(1-a）^2] * [St1 - 2St2 + St3]
         *  所以可得的预测值计算为:
         *  ^Yt+T = At + Bt*T + Ct*T^2
         **/
        // （1）获取实际观察值列表和最后一次的预测值
        //获取实际的观察数据列表
        List<Double> realParamList = tripleSmoothingEntity.getRealDataList();
        //获取三次平滑的预测值
        Double lastSinglePredictParam = tripleSmoothingEntity.getLastSinglePredictParam();
        Double lastSecondPredictParam = tripleSmoothingEntity.getLastSecondPredictParam();
        Double lastTriplePredictParam = tripleSmoothingEntity.getLastTriplePredictParam();
        // 获取总期数计算均方误差
        int totalTimes = realParamList.size();
        // 用于复位的
        Double resizeSecondPredict = tripleSmoothingEntity.getLastSecondPredictParam();
        Double resizeTriplePredict = tripleSmoothingEntity.getLastTriplePredictParam();
        int predictTime = tripleSmoothingEntity.getPredictTime();
        // 定义结果集合类
        List<Double> singleGapList = new ArrayList<>();
        List<Double> secondGapList = new ArrayList<>();
        List<Double> tripleGapList = new ArrayList<>();
        Map<Double,Double> singleGapMap = new HashMap<>();
        Map<Double,Double> secondGapMap = new HashMap<>();
        Map<Double,Double> tripleGapMap = new HashMap<>();
        // 保留一位小数
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        double yt_T = lastSecondPredictParam;
        double yt3_T = lastTriplePredictParam;

        //记录每个平滑系数 对应 每个数值预测的平均值
        //HashMap<Double, List<Double>> map = new HashMap<Double, List<Double>>();

        // （2）平滑值区间 [1~10]/10,先做一次平滑
        for (double i = 1; i < 10; i++) {
            System.out.println("---------------------平滑系数为"+i/10+"---------------------------");
            System.out.println("----------------------------"+lastSinglePredictParam+"---------------------------");
            System.out.println("----------------------------"+lastSecondPredictParam+"---------------------------");
            System.out.println("----------------------------"+lastTriplePredictParam+"---------------------------");

            //记录当前平滑系数 每个数据的三次预测值的平均值
            LinkedList<Double> avgList = new LinkedList<>();

            for (Double realData: realParamList) {
                double smoothParam = i/10; //平滑系数
                System.out.println("实际值是："
                        + realData+",一次平滑预测值是："
                        +lastSinglePredictParam+"，误差为："
                        + decimalFormat.format(Math.abs(realData-lastSinglePredictParam))
                        + "；二次平滑预测值是："+ yt_T +",误差为："
                        + decimalFormat.format(Math.abs(realData-yt_T))
                        + ";三次平滑预测值是：" + yt3_T+",误差为："
                        + decimalFormat.format(Math.abs(realData-yt3_T)));
                // 统计全部的误差
                singleGapList.add(Math.abs(realData-lastSinglePredictParam));
                secondGapList.add(Math.abs(realData-yt_T));
                tripleGapList.add(Math.abs(realData-yt3_T));

                // （3）获得一次平滑预测值
                lastSinglePredictParam = smoothParam * realData + (1-smoothParam)*lastSinglePredictParam;
                // 保留一位小数
                lastSinglePredictParam = Double.valueOf(decimalFormat.format(lastSinglePredictParam));
                System.out.println("一次平滑预测下次为："+lastSinglePredictParam);
                // （4）计算二次平滑值
                lastSecondPredictParam = smoothParam * lastSinglePredictParam + (1-smoothParam)*lastSecondPredictParam;
                double at = 2 * lastSinglePredictParam - lastSecondPredictParam;
                double bt = smoothParam/(1-smoothParam) * (lastSinglePredictParam - lastSecondPredictParam);
                // （4-1）计算出二次平滑预测值
                yt_T = at + bt * predictTime;
                System.out.println("二次平滑为："+lastSecondPredictParam+",下"+predictTime+"期的预测值为："+yt_T);

                // （5）计算三次平滑值
                lastTriplePredictParam = smoothParam * lastSecondPredictParam + (1-smoothParam)*lastTriplePredictParam;
                double at3 = 3*lastSinglePredictParam - 3*lastSecondPredictParam + lastTriplePredictParam;
                // (5-1)根据时间顺序分配权重
                double bt_s0 = 2 * (1-smoothParam)*(1-smoothParam);
                double bt_s1 = smoothParam/bt_s0;
                double bt_s2 = (6-5*smoothParam)*lastSinglePredictParam;
                double bt_s3 = 2*(5-4*smoothParam)*lastSecondPredictParam;
                double bt_s4 = (4-3*smoothParam)*lastTriplePredictParam;
                double bt_s5 = bt_s1 * (bt_s2 - bt_s3 + bt_s4);
                double bt3 = bt_s5;
                double ct_0 = smoothParam * smoothParam / bt_s0;
                double ct_1 = 2*lastSecondPredictParam;
                double ct_2 = lastSinglePredictParam-ct_1+lastTriplePredictParam;
                double ct_3 = ct_0 * ct_2;
                // (5-2)计算三次滑动的预期值
                yt3_T = at3 + bt3 * predictTime + ct_3 * predictTime * predictTime;
                System.out.println("三次平滑为："+lastTriplePredictParam+",下"+predictTime+"期的预测值为："+yt3_T);

            }

            // 计算MES均方误差
            double totalSingleGap = 0.0;
            double totalSecondGap = 0.0;
            double totalTripleGap = 0.0;
            for (Double singleGap: singleGapList) {
                totalSingleGap = totalSingleGap + singleGap * singleGap;
            }
            for (Double secondGap: secondGapList) {
                totalSecondGap = totalSecondGap + secondGap * secondGap;
            }
            for (Double tripleGap: tripleGapList) {
                totalTripleGap = totalTripleGap + tripleGap * tripleGap;
            }
            //记录每个数据三次平滑 平滑系数对应的均方差
            singleGapMap.put(i/10 , Math.sqrt(totalSingleGap)/totalTimes);
            secondGapMap.put(i/10 , Math.sqrt(totalSecondGap)/totalTimes);
            tripleGapMap.put(i/10 , Math.sqrt(totalTripleGap)/totalTimes);
            // 每更换一个平滑值，预估值都要复位
            lastSinglePredictParam = tripleSmoothingEntity.getLastSinglePredictParam();
            lastSecondPredictParam = tripleSmoothingEntity.getLastSecondPredictParam();
            lastTriplePredictParam = tripleSmoothingEntity.getLastTriplePredictParam();
            yt_T = resizeSecondPredict;
            yt3_T = resizeTriplePredict;
            // 清空当前list装的误差值
            singleGapList.clear();
            secondGapList.clear();
            tripleGapList.clear();
        }
        //保存各个平滑 的 均方差 和 平滑系数
        HashMap<Double, Double> singleMap = new HashMap<>();
        singleMap.put((singleGapMap.get(0.1)),0.1);
        singleMap.put((singleGapMap.get(0.2)),0.2);
        singleMap.put((singleGapMap.get(0.3)),0.3);
        singleMap.put((singleGapMap.get(0.4)),0.4);
        singleMap.put((singleGapMap.get(0.5)),0.5);
        singleMap.put((singleGapMap.get(0.6)),0.6);
        singleMap.put((singleGapMap.get(0.7)),0.7);
        singleMap.put((singleGapMap.get(0.8)),0.8);
        singleMap.put((singleGapMap.get(0.9)),0.9);
        //保存均方差
        double[] singleAvg = new double[9];
        singleAvg[0]=(singleGapMap.get(0.1));
        singleAvg[1]=(singleGapMap.get(0.2));
        singleAvg[2]=(singleGapMap.get(0.3));
        singleAvg[3]=(singleGapMap.get(0.4));
        singleAvg[4]=(singleGapMap.get(0.5));
        singleAvg[5]=(singleGapMap.get(0.6));
        singleAvg[6]=(singleGapMap.get(0.7));
        singleAvg[7]=(singleGapMap.get(0.8));
        singleAvg[8]=(singleGapMap.get(0.9));

        HashMap<Double, Double> secondMap = new HashMap<>();
        secondMap.put((secondGapMap.get(0.1)),0.1);
        secondMap.put((secondGapMap.get(0.2)),0.2);
        secondMap.put((secondGapMap.get(0.3)),0.3);
        secondMap.put((secondGapMap.get(0.4)),0.4);
        secondMap.put((secondGapMap.get(0.5)),0.5);
        secondMap.put((secondGapMap.get(0.6)),0.6);
        secondMap.put((secondGapMap.get(0.7)),0.7);
        secondMap.put((secondGapMap.get(0.8)),0.8);
        secondMap.put((secondGapMap.get(0.9)),0.9);
        //保存均方差
        double[] secondAvg = new double[9];
        secondAvg[0]=(secondGapMap.get(0.1));
        secondAvg[1]=(secondGapMap.get(0.2));
        secondAvg[2]=(secondGapMap.get(0.3));
        secondAvg[3]=(secondGapMap.get(0.4));
        secondAvg[4]=(secondGapMap.get(0.5));
        secondAvg[5]=(secondGapMap.get(0.6));
        secondAvg[6]=(secondGapMap.get(0.7));
        secondAvg[7]=(secondGapMap.get(0.8));
        secondAvg[8]=(secondGapMap.get(0.9));

        HashMap<Double, Double> tripleMap = new HashMap<>();
        tripleMap.put((tripleGapMap.get(0.1)),0.1);
        tripleMap.put((tripleGapMap.get(0.2)),0.2);
        tripleMap.put((tripleGapMap.get(0.3)),0.3);
        tripleMap.put((tripleGapMap.get(0.4)),0.4);
        tripleMap.put((tripleGapMap.get(0.5)),0.5);
        tripleMap.put((tripleGapMap.get(0.6)),0.6);
        tripleMap.put((tripleGapMap.get(0.7)),0.7);
        tripleMap.put((tripleGapMap.get(0.8)),0.8);
        tripleMap.put((tripleGapMap.get(0.9)),0.9);
        //保存均方差
        double[] tripleAvg = new double[9];
        tripleAvg[0]=(tripleGapMap.get(0.1));
        tripleAvg[1]=(tripleGapMap.get(0.2));
        tripleAvg[2]=(tripleGapMap.get(0.3));
        tripleAvg[3]=(tripleGapMap.get(0.4));
        tripleAvg[4]=(tripleGapMap.get(0.5));
        tripleAvg[5]=(tripleGapMap.get(0.6));
        tripleAvg[6]=(tripleGapMap.get(0.7));
        tripleAvg[7]=(tripleGapMap.get(0.8));
        tripleAvg[8]=(tripleGapMap.get(0.9));
        //给三次平滑的均方差排序
        Arrays.sort(singleAvg);
        Arrays.sort(secondAvg);
        Arrays.sort(tripleAvg);
        //记录三次平滑最小均方差对应的平滑系数
        Double singleMinAvg = singleMap.get(singleAvg[0]);
        Double secondMinAvg = secondMap.get(secondAvg[0]);
        Double tripleMinAvg = tripleMap.get(tripleAvg[0]);
        HashMap<Double, Double> minAvgMap = new HashMap<>();
        minAvgMap.put(singleAvg[0],singleMinAvg);
        minAvgMap.put(secondAvg[0],secondMinAvg);
        minAvgMap.put(tripleAvg[0],tripleMinAvg);
        //获取三次平滑 最小的均方差
        Double min = Math.min(singleAvg[0],Math.min(secondAvg[0],tripleAvg[0]));
        Double minCoefficient = minAvgMap.get(min);//最小均方差对应的平滑系数

        System.out.println("平滑的最小均方差为:"+min);
        System.out.println("对应的平滑系数为:"+minCoefficient);
        int smoothCount = 0;
        //判断是第几次平滑(1,2,3)
        if(singleGapMap.containsValue(min)){
            smoothCount=1;
        }else if(secondGapMap.containsValue(min)){
            smoothCount=2;
        }else{
            smoothCount=3;
        }

        System.out.println("一次平滑的各平滑系数的均方差"+singleGapMap.toString());
        System.out.println("二次平滑的各平滑系数的均方差"+secondGapMap.toString());
        System.out.println("三次平滑的各平滑系数的均方差"+tripleGapMap.toString());

        System.out.println("---------------------第"+smoothCount+"次平滑---------------------------");
        System.out.println("---------------------平滑系数为"+minCoefficient+"---------------------------");
        System.out.println("----------------------------"+lastSinglePredictParam+"---------------------------");
        System.out.println("----------------------------"+lastSecondPredictParam+"---------------------------");
        System.out.println("----------------------------"+lastTriplePredictParam+"---------------------------");

        Double res = 0.0;

        //根据smoothCount来进行1,2,3次平滑
        switch(smoothCount){
            case 1 : //进行一次平滑
                for (Double realData: realParamList) {
                    double smoothParam = minCoefficient; //平滑系数
                    System.out.println("实际值是："
                            + realData + ",一次平滑预测值是："
                            + lastSinglePredictParam + "，误差为："
                            + decimalFormat.format(Math.abs(realData - lastSinglePredictParam))
                            + "；二次平滑预测值是：" + yt_T + ",误差为："
                            + decimalFormat.format(Math.abs(realData - yt_T))
                            + ";三次平滑预测值是：" + yt3_T + ",误差为："
                            + decimalFormat.format(Math.abs(realData - yt3_T)));
                    // 统计全部的误差
                    singleGapList.add(Math.abs(realData - lastSinglePredictParam));
                    secondGapList.add(Math.abs(realData - yt_T));
                    tripleGapList.add(Math.abs(realData - yt3_T));

                    // （3）获得一次平滑预测值
                    lastSinglePredictParam = smoothParam * realData + (1 - smoothParam) * lastSinglePredictParam;
                    // 保留一位小数
                    lastSinglePredictParam = Double.valueOf(decimalFormat.format(lastSinglePredictParam));
                    System.out.println("一次平滑预测下次为：" + lastSinglePredictParam);
                    // （4）计算二次平滑值
                    lastSecondPredictParam = smoothParam * lastSinglePredictParam + (1 - smoothParam) * lastSecondPredictParam;
                    double at = 2 * lastSinglePredictParam - lastSecondPredictParam;
                    double bt = smoothParam / (1 - smoothParam) * (lastSinglePredictParam - lastSecondPredictParam);
                    // （4-1）计算出二次平滑预测值
                    yt_T = at + bt * predictTime;
                    System.out.println("二次平滑为：" + lastSecondPredictParam + ",下" + predictTime + "期的预测值为：" + yt_T);

                    // （5）计算三次平滑值
                    lastTriplePredictParam = smoothParam * lastSecondPredictParam + (1 - smoothParam) * lastTriplePredictParam;
                    double at3 = 3 * lastSinglePredictParam - 3 * lastSecondPredictParam + lastTriplePredictParam;
                    // (5-1)根据时间顺序分配权重
                    double bt_s0 = 2 * (1 - smoothParam) * (1 - smoothParam);
                    double bt_s1 = smoothParam / bt_s0;
                    double bt_s2 = (6 - 5 * smoothParam) * lastSinglePredictParam;
                    double bt_s3 = 2 * (5 - 4 * smoothParam) * lastSecondPredictParam;
                    double bt_s4 = (4 - 3 * smoothParam) * lastTriplePredictParam;
                    double bt_s5 = bt_s1 * (bt_s2 - bt_s3 + bt_s4);
                    double bt3 = bt_s5;
                    double ct_0 = smoothParam * smoothParam / bt_s0;
                    double ct_1 = 2 * lastSecondPredictParam;
                    double ct_2 = lastSinglePredictParam - ct_1 + lastTriplePredictParam;
                    double ct_3 = ct_0 * ct_2;
                    // (5-2)计算三次滑动的预期值
                    yt3_T = at3 + bt3 * predictTime + ct_3 * predictTime * predictTime;
                    System.out.println("三次平滑为：" + lastTriplePredictParam + ",下" + predictTime + "期的预测值为：" + yt3_T);
                    res = (lastSinglePredictParam+yt3_T+yt_T)/3;
                    System.out.println("---------一次平滑预测下"+predictTime+"期为:"+res);
                }
                log.info("一次平滑预测下"+predictTime+"期为:"+res);
                break;
            case 2 : //进行二次平滑
                for (Double realData: realParamList) {
                    double smoothParam = minCoefficient; //平滑系数
                    System.out.println("实际值是："
                            + realData + ",一次平滑预测值是："
                            + lastSinglePredictParam + "，误差为："
                            + decimalFormat.format(Math.abs(realData - lastSinglePredictParam))
                            + "；二次平滑预测值是：" + yt_T + ",误差为："
                            + decimalFormat.format(Math.abs(realData - yt_T))
                            + ";三次平滑预测值是：" + yt3_T + ",误差为："
                            + decimalFormat.format(Math.abs(realData - yt3_T)));
                    // 统计全部的误差
                    singleGapList.add(Math.abs(realData - lastSinglePredictParam));
                    secondGapList.add(Math.abs(realData - yt_T));
                    tripleGapList.add(Math.abs(realData - yt3_T));

                    // （3）获得一次平滑预测值
                    lastSinglePredictParam = smoothParam * realData + (1 - smoothParam) * lastSinglePredictParam;
                    // 保留一位小数
                    lastSinglePredictParam = Double.valueOf(decimalFormat.format(lastSinglePredictParam));
                    System.out.println("一次平滑预测下次为：" + lastSinglePredictParam);
                    // （4）计算二次平滑值
                    lastSecondPredictParam = smoothParam * lastSinglePredictParam + (1 - smoothParam) * lastSecondPredictParam;
                    double at = 2 * lastSinglePredictParam - lastSecondPredictParam;
                    double bt = smoothParam / (1 - smoothParam) * (lastSinglePredictParam - lastSecondPredictParam);
                    // （4-1）计算出二次平滑预测值
                    yt_T = at + bt * predictTime;
                    System.out.println("二次平滑为：" + lastSecondPredictParam + ",下" + predictTime + "期的预测值为：" + yt_T);

                    // （5）计算三次平滑值
                    lastTriplePredictParam = smoothParam * lastSecondPredictParam + (1 - smoothParam) * lastTriplePredictParam;
                    double at3 = 3 * lastSinglePredictParam - 3 * lastSecondPredictParam + lastTriplePredictParam;
                    // (5-1)根据时间顺序分配权重
                    double bt_s0 = 2 * (1 - smoothParam) * (1 - smoothParam);
                    double bt_s1 = smoothParam / bt_s0;
                    double bt_s2 = (6 - 5 * smoothParam) * lastSinglePredictParam;
                    double bt_s3 = 2 * (5 - 4 * smoothParam) * lastSecondPredictParam;
                    double bt_s4 = (4 - 3 * smoothParam) * lastTriplePredictParam;
                    double bt_s5 = bt_s1 * (bt_s2 - bt_s3 + bt_s4);
                    double bt3 = bt_s5;
                    double ct_0 = smoothParam * smoothParam / bt_s0;
                    double ct_1 = 2 * lastSecondPredictParam;
                    double ct_2 = lastSinglePredictParam - ct_1 + lastTriplePredictParam;
                    double ct_3 = ct_0 * ct_2;
                    // (5-2)计算三次滑动的预期值
                    yt3_T = at3 + bt3 * predictTime + ct_3 * predictTime * predictTime;
                    System.out.println("三次平滑为：" + lastTriplePredictParam + ",下" + predictTime + "期的预测值为：" + yt3_T);
                    res = yt_T;
                    System.out.println("---------二次平滑预测下"+predictTime+"期为:"+res);
                }
                log.info("二次平滑预测下"+predictTime+"期为:"+res);
                break; //可选
            default : //进行三次平滑
                for (Double realData: realParamList) {
                    double smoothParam = minCoefficient; //平滑系数
                    System.out.println("实际值是："
                            + realData + ",一次平滑预测值是："
                            + lastSinglePredictParam + "，误差为："
                            + decimalFormat.format(Math.abs(realData - lastSinglePredictParam))
                            + "；二次平滑预测值是：" + yt_T + ",误差为："
                            + decimalFormat.format(Math.abs(realData - yt_T))
                            + ";三次平滑预测值是：" + yt3_T + ",误差为："
                            + decimalFormat.format(Math.abs(realData - yt3_T)));
                    // 统计全部的误差
                    singleGapList.add(Math.abs(realData - lastSinglePredictParam));
                    secondGapList.add(Math.abs(realData - yt_T));
                    tripleGapList.add(Math.abs(realData - yt3_T));

                    // （3）获得一次平滑预测值
                    lastSinglePredictParam = smoothParam * realData + (1 - smoothParam) * lastSinglePredictParam;
                    // 保留一位小数
                    lastSinglePredictParam = Double.valueOf(decimalFormat.format(lastSinglePredictParam));
                    System.out.println("一次平滑预测下次为：" + lastSinglePredictParam);
                    // （4）计算二次平滑值
                    lastSecondPredictParam = smoothParam * lastSinglePredictParam + (1 - smoothParam) * lastSecondPredictParam;
                    double at = 2 * lastSinglePredictParam - lastSecondPredictParam;
                    double bt = smoothParam / (1 - smoothParam) * (lastSinglePredictParam - lastSecondPredictParam);
                    // （4-1）计算出二次平滑预测值
                    yt_T = at + bt * predictTime;
                    System.out.println("二次平滑为：" + lastSecondPredictParam + ",下" + predictTime + "期的预测值为：" + yt_T);

                    // （5）计算三次平滑值
                    lastTriplePredictParam = smoothParam * lastSecondPredictParam + (1 - smoothParam) * lastTriplePredictParam;
                    double at3 = 3 * lastSinglePredictParam - 3 * lastSecondPredictParam + lastTriplePredictParam;
                    // (5-1)根据时间顺序分配权重
                    double bt_s0 = 2 * (1 - smoothParam) * (1 - smoothParam);
                    double bt_s1 = smoothParam / bt_s0;
                    double bt_s2 = (6 - 5 * smoothParam) * lastSinglePredictParam;
                    double bt_s3 = 2 * (5 - 4 * smoothParam) * lastSecondPredictParam;
                    double bt_s4 = (4 - 3 * smoothParam) * lastTriplePredictParam;
                    double bt_s5 = bt_s1 * (bt_s2 - bt_s3 + bt_s4);
                    double bt3 = bt_s5;
                    double ct_0 = smoothParam * smoothParam / bt_s0;
                    double ct_1 = 2 * lastSecondPredictParam;
                    double ct_2 = lastSinglePredictParam - ct_1 + lastTriplePredictParam;
                    double ct_3 = ct_0 * ct_2;
                    // (5-2)计算三次滑动的预期值
                    yt3_T = at3 + bt3 * predictTime + ct_3 * predictTime * predictTime;
                    System.out.println("三次平滑为：" + lastTriplePredictParam + ",下" + predictTime + "期的预测值为：" + yt3_T);
                    res = (lastSinglePredictParam+yt_T+yt3_T)/3;
                    System.out.println("---------三次平滑预测下"+predictTime+"期为:"+res);
                }
                log.info("三次平滑预测下"+predictTime+"期为:"+res);
        }


        return res;


    }
}


