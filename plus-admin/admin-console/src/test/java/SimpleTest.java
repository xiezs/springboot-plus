import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.junit.Test;

public class SimpleTest {

  @Test
  public void testStream() {
    GetLastProductByJobsBean b1 = new GetLastProductByJobsBean();
    b1.setJobNo("JB05");
    b1.setOperNo(60);
    b1.setCreateBy("a");
    GetLastProductByJobsBean b2 = new GetLastProductByJobsBean();
    b2.setJobNo("JF00");
    b2.setOperNo(10);
    b2.setCreateBy("b");
    GetLastProductByJobsBean b3 = new GetLastProductByJobsBean();
    b3.setJobNo("JF00");
    b3.setOperNo(20);
    b3.setCreateBy("c");
    List<GetLastProductByJobsBean> lists = CollUtil.newArrayList(b1, b2, b3);

    Map<String, Map<Integer, String>> collect =
        lists.stream()
            .collect(
                Collectors.groupingBy(
                    GetLastProductByJobsBean::getJobNo,
                    Collectors.groupingBy(
                        k -> k.getOperNo(),
                        new Collector<GetLastProductByJobsBean, StringBuffer, String>() {
                          @Override
                          public Supplier<StringBuffer> supplier() {
                            // 相当于做一个容器
                            return () -> new StringBuffer();
                          }

                          @Override
                          public BiConsumer<StringBuffer, GetLastProductByJobsBean> accumulator() {
                            // 这里将真的值存放到上面的容器里
                            return (o, getLastProductByJobsBean) ->
                                o.append(getLastProductByJobsBean.getCreateBy());
                          }

                          @Override
                          public BinaryOperator<StringBuffer> combiner() {
                            // 这里冲突里就用第二个吧，其实用不到，不是map的容器
                            return (o, o2) -> o2;
                          }

                          @Override
                          public Function<StringBuffer, String> finisher() {
                            return o -> o.toString();
                          }

                          @Override
                          public Set<Characteristics> characteristics() {
                            return Collections.unmodifiableSet(
                                EnumSet.of(
                                    Collector.Characteristics.UNORDERED,
                                    Collector.Characteristics.IDENTITY_FINISH));
                          }
                        })));
    System.out.println(collect);
  }

  @Test
  public void music() {
    String signStr = "C,Db,D,Eb,E,F,F#,G,Ab,A,Bb,B";
    List<String> signList = StrUtil.split(signStr, ',');
    List<String> allSignList = new ArrayList<>();
    for (int i = 0; i <= 8; i++) {
      for (String sign : signList) {
        allSignList.add(sign + i);
      }
    }
    allSignList = CollUtil.sub(allSignList, 9, 97);
    printAll(allSignList);
    signStr =
        "C,Db,D,Eb,E,F,F#,G,Ab,A,Bb,B,C,Db,D,Eb,E,F,F#,G,Ab,A,Bb,B,C,Db,D,Eb,E,F,F#,G,Ab,A,Bb,B,C,Db,D,Eb,E,F,F#,G,Ab,A,Bb,B";
    signList = StrUtil.split(signStr, ',');
    int[] major = new int[] {2, 2, 1, 2, 2, 2, 1};
    int[] minor = new int[] {2, 1, 2, 2, 1, 2, 2};
    Map<String, Map<String, List<String>>> rsMaps = new LinkedHashMap<>();
    String majorStr = "Major";
    String minorStr = "Minor";
    rsMaps.put(majorStr, new LinkedHashMap<>());
    rsMaps.put(minorStr, new LinkedHashMap<>());
    for (int i = 0; i < 12; i++) {
      String lead = signList.get(i);
      List rs = new ArrayList();
      rs.add(lead);
      int previous = i;
      for (int j = 0; j < major.length; j++) {
        previous = previous + major[j];
        String t = signList.get(previous);
        rs.add(t);
      }
      rsMaps.get(majorStr).put(lead, CollUtil.newArrayList(rs));
      rs.clear();
      rs.add(lead);
      previous = i;
      for (int j = 0; j < minor.length; j++) {
        previous = previous + minor[j];
        String t = signList.get(previous);
        rs.add(t);
      }
      rsMaps.get(minorStr).put(lead, CollUtil.newArrayList(rs));
    }
    printRsmap(rsMaps);

    List<Map<String, List<String>>> relationMaps = new ArrayList<>();

    Set<Entry<String, List<String>>> majorEntries = rsMaps.get(majorStr).entrySet();
    Set<Entry<String, List<String>>> minorEntries = rsMaps.get(minorStr).entrySet();
    for (Entry<String, List<String>> majorEntry : majorEntries) {
      Map<String, List<String>> tempRelMap = new LinkedHashMap<>();
      String majorEntryKey = majorEntry.getKey();
      tempRelMap.put(majorEntryKey + "-" + majorStr, majorEntry.getValue());
      for (Entry<String, List<String>> minorEntry : minorEntries) {
        boolean isRelation = isRelation(majorEntry.getValue(), minorEntry.getValue());
        if (isRelation) {
          tempRelMap.put(minorEntry.getKey() + "-" + minorStr, minorEntry.getValue());
        }
      }
      relationMaps.add(tempRelMap);
    }
    printRelMap(relationMaps);
  }

  private void printRelMap(List<Map<String, List<String>>> relationMaps) {
    String majorStr = "Major";
    String minorStr = "Minor";
    for (Map<String, List<String>> relationMap : relationMaps) {
      for (Entry<String, List<String>> entry : relationMap.entrySet()) {
        String[] split = entry.getKey().split("-");
        String n = split[1].equals(majorStr)?"大调":"小调";
        System.out.println(split[0]+n+" : " +CollUtil.join(entry.getValue(), ","));
      }
      System.out.println("* * * ");
    }
  }

  private void printRsmap(Map<String, Map<String, List<String>>> rsMaps) {
    String majorStr = "Major";
    String minorStr = "Minor";
    Map<String, List<String>> majorMaps = rsMaps.get(majorStr);
    System.out.println("所有的自然大调：");
    for (Entry<String, List<String>> entry : majorMaps.entrySet()) {
      System.out.println(entry.getKey()+" : "+CollUtil.join(entry.getValue(), ","));
    }
    Map<String, List<String>> minorMaps = rsMaps.get(minorStr);
    System.out.println("所有的自然小调：");
    for (Entry<String, List<String>> entry : minorMaps.entrySet()) {
      System.out.println(entry.getKey()+" : "+CollUtil.join(entry.getValue(), ","));
    }
  }

  private void printAll(List<String> allSignList) {
    System.out.println("88键钢琴键位从左往右："+CollUtil.join(allSignList, ","));
    System.out.println("A4是标准音440HZ");
  }

  // 降E大调音阶是<Eb、F、G、Ab、Bb、C、D>，c小调的音阶是<C、D、Eb、F、G、Ab、Bb>
  private boolean isRelation(List<String> majors, List<String> minors) {
    List<String> copyMajors = CollUtil.newArrayList(majors);
    copyMajors.remove(copyMajors.size() - 1);
    List<String> copyMiniors = CollUtil.newArrayList(minors);
    copyMiniors.remove(copyMiniors.size() - 1);
    for (int i = 0; i < copyMajors.size(); i++) {
      String major = copyMajors.get(i);
      copyMiniors.remove(major);
    }
    return copyMiniors.isEmpty();
  }
}
