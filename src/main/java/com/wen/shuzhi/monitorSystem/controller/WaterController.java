package com.wen.shuzhi.monitorSystem.controller;

import com.wen.shuzhi.monitorSystem.entity.TripleSmoothingEntity;
import com.wen.shuzhi.monitorSystem.entity.WaterInfo;
import com.wen.shuzhi.monitorSystem.service.Impl.WaterServiceImpl;
import com.wen.shuzhi.monitorSystem.service.WaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/water")
@Slf4j
public class WaterController {
    @Autowired
    private WaterServiceImpl waterService;

    @Autowired
    @Lazy
    private TripleSmoothController tripleSmoothController;


    @GetMapping("/list")
    public List<WaterInfo> getWaterInfo(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        return waterService.list();
    }


    @RequestMapping("/forecastChroma")
    public List<WaterInfo> forecastChroma(Model model){

        List<WaterInfo> waterInfos = waterService.list();
        List<Double> chromaList = new ArrayList<>();
        //获取所有数据的色度属性
        for(WaterInfo waterInfo : waterInfos){
            chromaList.add(waterInfo.getChroma());
        }
        Double init = chromaList.get(0);
        log.info("init="+init);

        //获取 三次平滑的下一天的预测值 的平均值
        TripleSmoothingEntity tripleSmoothingEntity1 = new TripleSmoothingEntity(chromaList, init, init, init, 1);
        Double nextOneDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity1, model);


        //获取 三次平滑的下两天的预测值 的平均值
        chromaList.add(nextOneDay);
        TripleSmoothingEntity tripleSmoothingEntity2 = new TripleSmoothingEntity(chromaList, init, init, init, 1);
        Double nextTwoDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity2, model);


        //获取 三次平滑的下三天的预测值 的平均值
        chromaList.add(nextTwoDay);
        TripleSmoothingEntity tripleSmoothingEntity3 = new TripleSmoothingEntity(chromaList, init, init, init, 1);
        Double nextThirdDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity3, model);


        //获取 三次平滑的下三天的预测值 的平均值
        chromaList.add(nextThirdDay);
        TripleSmoothingEntity tripleSmoothingEntity4 = new TripleSmoothingEntity(chromaList, init, init, init, 1);
        Double nextFourDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity4, model);


        //获取 三次平滑的下三天的预测值 的平均值
        chromaList.add(nextFourDay);
        TripleSmoothingEntity tripleSmoothingEntity5 = new TripleSmoothingEntity(chromaList, init, init, init, 1);
        Double nextFiveDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity5, model);

        log.info("预测下一期的值==="+nextOneDay);
        log.info("预测下二期的值==="+nextTwoDay);
        log.info("预测下三期的值==="+nextThirdDay);
        log.info("预测下四期的值==="+nextFourDay);
        log.info("预测下五期的值==="+nextFiveDay);

        //封装为对象
        ArrayList<WaterInfo> forecastList = new ArrayList<>();
        forecastList.add(new WaterInfo(nextOneDay,0.0,0.0,0.0));
        forecastList.add(new WaterInfo(nextTwoDay,0.0,0.0,0.0));
        forecastList.add(new WaterInfo(nextThirdDay,0.0,0.0,0.0));
        forecastList.add(new WaterInfo(nextFourDay,0.0,0.0,0.0));
        forecastList.add(new WaterInfo(nextFiveDay,0.0,0.0,0.0));
        log.info(forecastList.toString());
        return forecastList;
    }

    @RequestMapping("/forecastPH")
    public List<WaterInfo> forecastPH(Model model){

        List<WaterInfo> waterInfos = waterService.list();
        List<Double> pHList = new ArrayList<>();
        //获取所有数据的ph属性
        for(WaterInfo waterInfo : waterInfos){
            pHList.add(waterInfo.getPh());
        }
        Double init = pHList.get(0);
        log.info("init="+init);

        //获取 三次平滑的下一天的预测值 的平均值
        TripleSmoothingEntity tripleSmoothingEntity1 = new TripleSmoothingEntity(pHList, init, init, init, 1);
        Double nextOneDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity1, model);


        //获取 三次平滑的下两天的预测值 的平均值
        pHList.add(nextOneDay);
        TripleSmoothingEntity tripleSmoothingEntity2 = new TripleSmoothingEntity(pHList, init, init, init, 1);
        Double nextTwoDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity2, model);


        //获取 三次平滑的下三天的预测值 的平均值
        pHList.add(nextTwoDay);
        TripleSmoothingEntity tripleSmoothingEntity3 = new TripleSmoothingEntity(pHList, init, init, init, 1);
        Double nextThirdDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity3, model);


        //获取 三次平滑的下三天的预测值 的平均值
        pHList.add(nextThirdDay);
        TripleSmoothingEntity tripleSmoothingEntity4 = new TripleSmoothingEntity(pHList, init, init, init, 1);
        Double nextFourDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity4, model);


        //获取 三次平滑的下三天的预测值 的平均值
        pHList.add(nextFourDay);
        TripleSmoothingEntity tripleSmoothingEntity5 = new TripleSmoothingEntity(pHList, init, init, init, 1);
        Double nextFiveDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity5, model);

        log.info("预测下一期的值==="+nextOneDay);
        log.info("预测下二期的值==="+nextTwoDay);
        log.info("预测下三期的值==="+nextThirdDay);
        log.info("预测下四期的值==="+nextFourDay);
        log.info("预测下五期的值==="+nextFiveDay);


        //封装为对象
        ArrayList<WaterInfo> forecastList = new ArrayList<>();
        forecastList.add(new WaterInfo(0.0,nextOneDay,0.0,0.0));
        forecastList.add(new WaterInfo(0.0,nextTwoDay,0.0,0.0));
        forecastList.add(new WaterInfo(0.0,nextThirdDay,0.0,0.0));
        forecastList.add(new WaterInfo(0.0,nextFourDay,0.0,0.0));
        forecastList.add(new WaterInfo(0.0,nextFiveDay,0.0,0.0));
        log.info(forecastList.toString());
        return forecastList;
    }

    @RequestMapping("/forecastElectricalConductivity")
    public List<WaterInfo> forecastElectricalConductivity(Model model){

        List<WaterInfo> waterInfos = waterService.list();
        List<Double> ElectricalConductivityList = new ArrayList<>();
        //获取所有数据的色度属性
        for(WaterInfo waterInfo : waterInfos){
            ElectricalConductivityList.add(waterInfo.getElectricalConductivity());
        }
        Double init = ElectricalConductivityList.get(0);
        log.info("init="+init);

        //获取 三次平滑的下一天的预测值 的平均值
        TripleSmoothingEntity tripleSmoothingEntity1 = new TripleSmoothingEntity(ElectricalConductivityList, init, init, init, 1);
        Double nextOneDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity1, model);


        //获取 三次平滑的下两天的预测值 的平均值
        ElectricalConductivityList.add(nextOneDay);
        TripleSmoothingEntity tripleSmoothingEntity2 = new TripleSmoothingEntity(ElectricalConductivityList, init, init, init, 1);
        Double nextTwoDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity2, model);


        //获取 三次平滑的下三天的预测值 的平均值
        ElectricalConductivityList.add(nextTwoDay);
        TripleSmoothingEntity tripleSmoothingEntity3 = new TripleSmoothingEntity(ElectricalConductivityList, init, init, init, 1);
        Double nextThirdDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity3, model);


        //获取 三次平滑的下三天的预测值 的平均值
        ElectricalConductivityList.add(nextThirdDay);
        TripleSmoothingEntity tripleSmoothingEntity4 = new TripleSmoothingEntity(ElectricalConductivityList, init, init, init, 1);
        Double nextFourDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity4, model);


        //获取 三次平滑的下三天的预测值 的平均值
        ElectricalConductivityList.add(nextFourDay);
        TripleSmoothingEntity tripleSmoothingEntity5 = new TripleSmoothingEntity(ElectricalConductivityList, init, init, init, 1);
        Double nextFiveDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity5, model);

        log.info("预测下一期的值==="+nextOneDay);
        log.info("预测下二期的值==="+nextTwoDay);
        log.info("预测下三期的值==="+nextThirdDay);
        log.info("预测下四期的值==="+nextFourDay);
        log.info("预测下五期的值==="+nextFiveDay);


        //封装为对象
        ArrayList<WaterInfo> forecastList = new ArrayList<>();
        forecastList.add(new WaterInfo(0.0,0.0,nextOneDay,0.0));
        forecastList.add(new WaterInfo(0.0,0.0,nextTwoDay,0.0));
        forecastList.add(new WaterInfo(0.0,0.0,nextThirdDay,0.0));
        forecastList.add(new WaterInfo(0.0,0.0,nextFourDay,0.0));
        forecastList.add(new WaterInfo(0.0,0.0,nextFiveDay,0.0));
        log.info(forecastList.toString());
        return forecastList;
    }

    @RequestMapping("/forecastDissolvedOxygen")
    public List<WaterInfo> forecastDissolvedOxygen(Model model){

        List<WaterInfo> waterInfos = waterService.list();
        List<Double> DissolvedOxygenList = new ArrayList<>();
        //获取所有数据的色度属性
        for(WaterInfo waterInfo : waterInfos){
            DissolvedOxygenList.add(waterInfo.getDissolvedOxygen());
        }
        Double init = DissolvedOxygenList.get(0);
        log.info("init="+init);

        //获取 三次平滑的下一天的预测值 的平均值
        TripleSmoothingEntity tripleSmoothingEntity1 = new TripleSmoothingEntity(DissolvedOxygenList, init, init, init, 1);
        Double nextOneDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity1, model);


        //获取 三次平滑的下两天的预测值 的平均值
        DissolvedOxygenList.add(nextOneDay);
        TripleSmoothingEntity tripleSmoothingEntity2 = new TripleSmoothingEntity(DissolvedOxygenList, init, init, init, 1);
        Double nextTwoDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity2, model);


        //获取 三次平滑的下三天的预测值 的平均值
        DissolvedOxygenList.add(nextTwoDay);
        TripleSmoothingEntity tripleSmoothingEntity3 = new TripleSmoothingEntity(DissolvedOxygenList, init, init, init, 1);
        Double nextThirdDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity3, model);


        //获取 三次平滑的下三天的预测值 的平均值
        DissolvedOxygenList.add(nextThirdDay);
        TripleSmoothingEntity tripleSmoothingEntity4 = new TripleSmoothingEntity(DissolvedOxygenList, init, init, init, 1);
        Double nextFourDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity4, model);


        //获取 三次平滑的下三天的预测值 的平均值
        DissolvedOxygenList.add(nextFourDay);
        TripleSmoothingEntity tripleSmoothingEntity5 = new TripleSmoothingEntity(DissolvedOxygenList, init, init, init, 1);
        Double nextFiveDay = tripleSmoothController.tripleExponentialSmoothingMethod(tripleSmoothingEntity5, model);

        log.info("预测下一期的值==="+nextOneDay);
        log.info("预测下二期的值==="+nextTwoDay);
        log.info("预测下三期的值==="+nextThirdDay);
        log.info("预测下四期的值==="+nextFourDay);
        log.info("预测下五期的值==="+nextFiveDay);


        //封装为对象
        ArrayList<WaterInfo> forecastList = new ArrayList<>();
        forecastList.add(new WaterInfo(0.0,0.0,0.0,nextOneDay));
        forecastList.add(new WaterInfo(0.0,0.0,0.0,nextTwoDay));
        forecastList.add(new WaterInfo(0.0,0.0,0.0,nextThirdDay));
        forecastList.add(new WaterInfo(0.0,0.0,0.0,nextFourDay));
        forecastList.add(new WaterInfo(0.0,0.0,0.0,nextFiveDay));
        log.info(forecastList.toString());
        return forecastList;
    }

}
