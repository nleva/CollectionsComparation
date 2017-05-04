package ru.sendto.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections4.map.UnmodifiableMap;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;

public class Main {

	static long[][] times= new long[20][1000];
	
	public static void countTime(Runnable r, int expNum, int indx){
		long ts = System.nanoTime();
		r.run();
		times[expNum][indx]=System.nanoTime()-ts;
	}
	
	public static void main(String[] args) {
		HashMap<String, List<Consumer>> hashMap = new HashMap<>();
		HashMap<String, List<Consumer>> hashMap2 = new HashMap<>();
		
		Reflections r = new Reflections(new SubTypesScanner(false));
		final Set<String> allTypes = r.getAllTypes();
		// printAllTypes(allTypes);
		final ArrayList<Consumer> list = new ArrayList<>();
		list.add(e->System.out.println(e));
		String[] types = new String[allTypes.size()];
		allTypes.toArray(types);

		int expNum = 0;
		mapPutTest(hashMap, list, types, expNum);
//		ImmutableListMultimap<String, Consumer> immutableListMultimap = builder
		multimapPutTest(list, types);
		ImmutableListMultimap<String, Consumer> guavaMap = putToMultimap(list, types);// builder.build();

		unmodifiableMapPutTest(hashMap,2);
		Map<String, List<Consumer>> apacheMap = UnmodifiableMap.unmodifiableMap(hashMap);
		gsPutTest(hashMap,3);
		com.gs.collections.impl.UnmodifiableMap<String, List<Consumer>> gsMap = new com.gs.collections.impl.UnmodifiableMap<>(hashMap);
	
		mapPutTest(hashMap2, list, types, 9);
		
		System.out.println(hashMap.size());
		System.out.println(guavaMap.size());
		System.out.println(apacheMap.size());
		System.out.println(gsMap.size());
		
		
		
		

		int totalTypes = types.length;
//		String[] testKeys = new String[10_000];
//		for(int i=0 ; i< 10_000; i++){
//			testKeys[i]=types[(int) (Math.random()*totalTypes)];
//		}
		mapGetFullTest(apacheMap, types, 6);
		mapGetFullTest(hashMap, types, 4);
		mapGetFullTest(guavaMap.asMap(), types, 5);
		mapGetFullTest(gsMap, types, 7);
		mapGetFullTest(hashMap2, types, 8);
		
		

		Arrays.sort(times[0]);
		Arrays.sort(times[1]);

		Arrays.sort(times[2]);
		Arrays.sort(times[3]);
		
		Arrays.sort(times[4]);
		Arrays.sort(times[5]);
		Arrays.sort(times[6]);
		Arrays.sort(times[7]);
		Arrays.sort(times[8]);
		Arrays.sort(times[9]);

		System.out.println(times[0][500]);
		System.out.println(times[9][500]);
		System.out.println(times[1][500]);
		System.out.println(times[2][500]);
		System.out.println(times[3][500]);
		System.out.println("hashmap "+times[4][500]);
		System.out.println("guava   "+times[5][500]);
		System.out.println("apache  "+times[6][500]);
		System.out.println("gs      "+times[7][500]);
		System.out.println("hm2     "+times[8][500]);
		
		System.out.println(guavaMap.get(types[0]));
		
		// map.put(key, value)
	}

	private static void mapGetFullTest(Map<String, ?> hashMap, String[] types, int expNum) {
		for(int i=0;i<1000;i++){
			countTime(()->mapGetTest(hashMap, types), expNum, i);
		}
	}

	private static void mapGetTest(Map<String, ?> hashMap, String[] types) {
		for(int i=0 ; i<types.length; i++){
			hashMap.get(types[i]);
		}
	}

	@SuppressWarnings("rawtypes")
	private static void gsPutTest(HashMap<String, List<Consumer>> hashMap, int expNum) {
		for(int i=0 ; i < 1000; i++){
			countTime(()->newGSCollection(hashMap),expNum,i);
		}
	}

	private static void newGSCollection(HashMap<String, List<Consumer>> hashMap) {
		new com.gs.collections.impl.UnmodifiableMap<>(hashMap);
	}

	private static void unmodifiableMapPutTest(Map<String, List<Consumer>> hashMap, int expNum) {
		for(int i = 0 ; i<1000 ; i++){
			countTime(()->UnmodifiableMap.unmodifiableMap(hashMap),expNum,i);
		}
	}

	private static void multimapPutTest(final ArrayList<Consumer> list, String[] types) {
		for(int i=0 ; i< 1000; i++){
			
			countTime(()->putToMultimap(list, types),1,i);
		}
	}

	private static ImmutableListMultimap<String, Consumer> putToMultimap(final ArrayList<Consumer> list, String[] types) {
		Builder<String, Consumer> builder = ImmutableListMultimap.builder();
		putAllToBuilder(list, types, builder);
		return builder.build();
	}

	private static void putAllToBuilder(final ArrayList<Consumer> list, String[] types,
			Builder<String, Consumer> builder) {
		for (String type : types) {
			builder.putAll(type, list);
		}
	}

	private static void mapPutTest(HashMap<String, List<Consumer>> map, final ArrayList<Consumer> list, String[] types,
			final int expNum) {
		for (int i = expNum; i < 1_000; i++) {
			countTime(()->fillMap(map, list, types),expNum,i);
			map.clear();
		}
		fillMap(map, list, types);
		
	}

	private static void fillMap(HashMap<String, List<Consumer>> map, final ArrayList<Consumer> list, String[] types) {
		for (String type : types) {
			map.put(type, list);
		}
	}

	private static void printAllTypes(final Set<String> allTypes) {
		allTypes.forEach(t -> System.out.println(t));
		System.out.println(allTypes.size());
	}
}
