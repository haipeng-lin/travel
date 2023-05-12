package com.wen.shuzhi.rusticTourism.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 两两对比函数
public class CosineSimilarity {

    // 两两对比函数
    public static Double getCosineSimilarity(List<String> wordListA,List<String> wordListB){

        List<Double> vectorA = new ArrayList<>();
        List<Double> vectorB = new ArrayList<>();
        // 将关键词数组转换为词向量并保存在 vectorA 和 vectorB 中
        convertWordList2Vector(wordListA,wordListB,vectorA,vectorB);

        // 计算向量夹角的余弦值
        double cosine = Double.parseDouble(String.format("%.4f",countCosine(vectorA,vectorB)));
        return cosine;
    }


    /**
     * @Description : 将单词数组转换为单词向量，结果保存在 vectorA 和 vectorB 里
     * @param wordListA : 文本 A 的单词数组
     * @param wordListB : 文本 B 的单词数组
     * @param vectorA   : 文本 A 转换成为的向量 A
     * @param vectorB   : 文本 B 转换成为的向量 B
     * @return vocabulary : 词汇表
     */
    private static List<String> convertWordList2Vector(List<String> wordListA,List<String> wordListB,List<Double> vectorA,List<Double> vectorB){
        // 词汇表
        List<String> vocabulary = new ArrayList<>();

        // 获取词汇表 wordListA 的频率表，并同时建立词汇表
        Map<String,Double> frequencyTableA = buildFrequencyTable(wordListA, vocabulary);

        // 获取词汇表 wordListB 的频率表，并同时建立词汇表
        Map<String,Double> frequencyTableB = buildFrequencyTable(wordListB, vocabulary);

        // 根据频率表得到向量
        getWordVectorFromFrequencyTable(frequencyTableA,vectorA,vocabulary);
        getWordVectorFromFrequencyTable(frequencyTableB,vectorB,vocabulary);

        return vocabulary;
    }

    /**
     * @param wordList：单词数组
     * @param vocabulary: 词汇表
     * @return Map<String,Double>: key为单词，value为频率
     * @Description 建立词汇表 wordList 的频率表，并同时建立词汇表
     */
    private static Map<String,Double> buildFrequencyTable(List<String> wordList,List<String> vocabulary){
        // 先建立频数表
        Map<String,Integer> countTable = new HashMap<>();
        for (String word : wordList) {
            if(countTable.containsKey(word)){
                countTable.put(word,countTable.get(word)+1);
            }
            else{
                countTable.put(word,1);
            }
            // 词汇表中是无重复元素的，所以只在 vocabulary 中没有该元素时才加入
            if(!vocabulary.contains(word)){
                vocabulary.add(word);
            }
        }
        // totalCount 用于记录词出现的总次数
        int totalCount = wordList.size();
        // 将频数表转换为频率表
        Map<String,Double> frequencyTable = new HashMap<>();
        for (String key : countTable.keySet()) {
            frequencyTable.put(key,(double)countTable.get(key)/totalCount);
        }
        return frequencyTable;
    }

    /**
     * @param frequencyTable : 频率表
     * @param wordVector     : 转换后的词向量
     * @param vocabulary     : 词汇表
     * @Description 根据词汇表和文本的频率表计算词向量，最后 wordVector 和 vocabulary 应该是同维的
     */
    private static void getWordVectorFromFrequencyTable(Map<String,Double> frequencyTable,List<Double> wordVector,List<String> vocabulary){
        for (String word : vocabulary) {
            double value = 0.0;
            if(frequencyTable.containsKey(word)){
                value = frequencyTable.get(word);
            }
            wordVector.add(value);
        }
    }


    /**
     * @Description 计算向量 A 和向量 B 的夹角余弦值
     * @param vectorA   : 词向量 A
     * @param vectorB   : 词向量 B
     * @return
     */
    private static double countCosine(List<Double> vectorA,List<Double> vectorB){
        // 分别计算向量的平方和
        double sqrtA = countSquareSum(vectorA);
        double sqrtB = countSquareSum(vectorB);

        // 计算向量的点积
        double dotProductResult = 0.0;
        for(int i = 0;i < vectorA.size();i++){
            dotProductResult += vectorA.get(i) * vectorB.get(i);
        }

        return dotProductResult/(sqrtA*sqrtB);
    }

    // 计算向量平方和的开方
    private static double countSquareSum(List<Double> vector){
        double result = 0.0;
        for (Double value : vector) {
            result += value*value;
        }
        return Math.sqrt(result);
    }
}
