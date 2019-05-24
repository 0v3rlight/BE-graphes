package org.insa.algo.algo_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.algo.shortestpath.AStarAlgorithm;
import org.insa.algo.shortestpath.BellmanFordAlgorithm;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.shortestpath.ShortestPathSolution;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.io.BinaryGraphReader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DjikstraAStarTest
{
	private static final File MAP_DIRECTORY = new File("/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps");
	private static final String MAP_EXTENSION = "mapgr";
	private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
	private static final boolean USE_ORACLE = true;
	private static final int MAX_FILE_SIZE_WITH_ORACLE = 1024 * 1024;
	private static final float EPSILON = 0.00001f;

	private static MapCache mapCache = new MapCache();
	private static final TestsInfos testsInfos = new TestsInfos();
	
	private static class TestsInfos
	{
		private int totalTestCount;
		private int oracleTestCount;
		private int mapCount;
		
		public void tested(boolean oracle)
		{
			totalTestCount++;
			if(oracle)
			{
				oracleTestCount++;
			}
		}
		
		@Override
		public String toString()
		{
			String format = "Djikstra & A-Star tests finished (%d tests on %d maps realised, including %d tests with oracle algorithm)";
			return String.format(format, totalTestCount, mapCount, oracleTestCount);
		}
	}

	private static class MapCache
	{
		private File file;
		private Graph map;

		public Graph load(File file) throws IOException
		{
			if (!file.equals(this.file))
			{
				this.file = file;
				System.out.println("Loading " + file.getName() + "...");
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				map = new BinaryGraphReader(in).read();
				in.close();
			}
			return map;
		}
	}

	enum Nature
	{
		DISTANCE(0), TIME(2);

		public final int arcInspectorId;

		Nature(int arcInspectorId)
		{
			this.arcInspectorId = arcInspectorId;
		}
	}
	
	@FunctionalInterface
	private static interface AlgorithmProvider
	{
		public ShortestPathAlgorithm provide(ShortestPathData data);
	}

	protected static class TestParameters
	{

		public final File map;
		public final Nature nature;
		public final int seed;
		public final boolean nullLength;
		public final AlgorithmProvider algorithmProvider;

		public TestParameters(File map, Nature nature, int seed, boolean nullLength, AlgorithmProvider algorithmProvider)
		{
			this.map = map;
			this.nature = nature;
			this.seed = seed;
			this.nullLength = nullLength;
			this.algorithmProvider = algorithmProvider;
		}
	};

	@Parameters
	public static Collection<Object> data()
	{
		Collection<Object> objects = new ArrayList<>();

		File[] files = MAP_DIRECTORY.listFiles();
		for (File f : files)
		{
			if (f.isFile() && f.getName().toLowerCase().endsWith(MAP_EXTENSION) && f.length() <= MAX_FILE_SIZE)
			{
				testsInfos.mapCount++;
				for(AlgorithmProvider algorithmProvider : new AlgorithmProvider[] {DijkstraAlgorithm::new, AStarAlgorithm::new})
				{
					for (int seed = 0; seed < 5; seed++)
					{
						objects.add(new TestParameters(f, Nature.DISTANCE, seed, false, algorithmProvider));
						objects.add(new TestParameters(f, Nature.TIME, seed, false, algorithmProvider));
					}
					objects.add(new TestParameters(f, Nature.DISTANCE, 0, true, algorithmProvider));
					objects.add(new TestParameters(f, Nature.TIME, 0, true, algorithmProvider));
				}
			}
		}
		return objects;
	}

	@Parameter
	public TestParameters parameters;

	private Graph map;
	private Random random;
	private Node origin;
	private Node destination;
	private ShortestPathSolution solution;
	private ShortestPathSolution oracle;

	@Before
	public void init()
	{
		try
		{
			map = mapCache.load(parameters.map);
			random = new Random(parameters.seed);
			origin = getRandomNode();
			destination = getRandomNode();
			if(parameters.nullLength)
			{
				destination = origin;
			}
			ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(parameters.nature.arcInspectorId);
			solution = parameters.algorithmProvider.provide(new ShortestPathData(map, origin, destination, arcInspector)).run();
			if(USE_ORACLE && parameters.map.length() <= MAX_FILE_SIZE_WITH_ORACLE)
			{
				oracle = new BellmanFordAlgorithm(new ShortestPathData(map, origin, destination, arcInspector)).run();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public Node getRandomNode()
	{
		return map.get(random.nextInt(map.getNodes().size()));
	}

	@Test
	public void testIsValid()
	{
		testsInfos.tested(false);
		if(solution.isFeasible())
		{
			assertTrue(solution.getPath().isValid());
		}
	}

	@Test
	public void testFeasible()
	{
		if(oracle != null)
		{
			testsInfos.tested(true);
			assertEquals(solution.isFeasible(), oracle.isFeasible());
		}
	}

	@Test
	public void testIsOptimal()
	{
		if(oracle != null)
		{
			testsInfos.tested(true);
			if(solution.isFeasible() &&  oracle.isFeasible())
			{
				if(parameters.nature == Nature.DISTANCE)
				{
					assertEquals(solution.getPath().getLength(), solution.getPath().getLength(), EPSILON);
				}
				else if(parameters.nature == Nature.TIME)
				{
					assertEquals(solution.getPath().getMinimumTravelTime(), solution.getPath().getMinimumTravelTime(), EPSILON);
				}
			}
		}
	}
	
	@AfterClass
	public static void infos()
	{
		System.out.println(testsInfos);
	}
}
