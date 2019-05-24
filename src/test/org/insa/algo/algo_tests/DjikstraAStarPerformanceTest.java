package org.insa.algo.algo_tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.algo.algo_tests.DjikstraAStarTest.AlgorithmProvider;
import org.insa.algo.algo_tests.DjikstraAStarTest.MapCache;
import org.insa.algo.algo_tests.DjikstraAStarTest.Nature;
import org.insa.algo.shortestpath.AStarAlgorithm;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.shortestpath.ShortestPathSolution;
import org.insa.graph.Graph;
import org.insa.graph.Node;

public class DjikstraAStarPerformanceTest
{
	private static final boolean CREATE_SCENARIOS = true;
	private static final boolean TEST_SCENARIOS = true;
	private static final File SCENARIOS_DIRECTORY = new File("jeux_de_tests");
	private static final File RESULTS_DIRECTORY = new File("resultats_de_tests");
	private static final File MAP_DIRECTORY = new File("/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps");
	private static final String MAP_EXTENSION = ".mapgr";
	private static final int MAX_MAP_FILE_SIZE = 10 * 1024 * 1024;
	private static final int PATH_COUNT_PER_MAP = 10;
	private static final int SEED = 0;

	private static final MapCache mapCache = new MapCache();

	public static void main(String[] args)
	{
		if (CREATE_SCENARIOS)
		{
			createScenarios();
			System.out.println("Scenario creation finished");
		}
		if (TEST_SCENARIOS)
		{
			testScenarios();
			System.out.println("Scenario test finished");
		}
	}

	private static class Scenario
	{
		private Nature nature;
		private int originId;
		private int destinationId;
		private Graph map;
		private ShortestPathSolution solution;

		public Scenario(Graph map, Nature nature)
		{
			this.map = map;
			this.nature = nature;
		}

		public Scenario(File map, Nature nature, int originId, int destinationId) throws IOException
		{
			this.map = mapCache.load(map);
			this.nature = nature;
			this.originId = originId;
			this.destinationId = destinationId;
		}

		public void test(AlgorithmProvider algorithmProvider, StringBuilder out)
		{
			ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(nature.arcInspectorId);
			Node origin = map.get(originId);
			Node destination = map.get(destinationId);
			solution = algorithmProvider.provide(new ShortestPathData(map, origin, destination, arcInspector)).run();
			out.append(String.format("%d %d %dms\n", originId, destinationId, solution.getSolvingTime().toMillis()));

		}

		public boolean isValid()
		{
			Node origin = map.get(originId);
			Node destination = map.get(destinationId);
			ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(nature.arcInspectorId);
			return new AStarAlgorithm(new ShortestPathData(map, origin, destination, arcInspector)).run().isFeasible();
		}
	}

	private static void testScenarios()
	{
		File[] files = SCENARIOS_DIRECTORY.listFiles();
		for (File f : files)
		{
			try
			{
				String[] lines = new String(Files.readAllBytes(f.toPath())).split("\n");
				String mapName = lines[0];
				File map = new File(MAP_DIRECTORY.getAbsolutePath() + "/" + mapName + MAP_EXTENSION);
				int natureId = Integer.parseInt(lines[1]);
				Nature nature = new Nature[]
				{ Nature.DISTANCE, Nature.TIME }[natureId];
				int pathCountPerMap = lines.length - 2;
				int algorithmId = 0;
				for (AlgorithmProvider algorithmProvider : new AlgorithmProvider[]
				{ DijkstraAlgorithm::new, AStarAlgorithm::new })
				{
					String algorithmStr = new String[]
					{ "djikstra", "astar" }[algorithmId];
					StringBuilder content = new StringBuilder();
					content.append(mapName + "\n");
					content.append(natureId + "\n");
					content.append(pathCountPerMap + "\n");
					content.append(algorithmStr + "\n");
					for (int i = 0; i < pathCountPerMap; i++)
					{
						String[] pair = lines[i + 2].split(" ");
						int originId = Integer.parseInt(pair[0]);
						int destinationId = Integer.parseInt(pair[1]);
						Scenario s = new Scenario(map, nature, originId, destinationId);
						s.test(algorithmProvider, content);
					}
					File file = new File(RESULTS_DIRECTORY.getAbsolutePath() + "/"
							+ getScenarioResultFileName(f, natureId, pathCountPerMap, algorithmStr));
					Files.write(file.toPath(), content.toString().getBytes(), StandardOpenOption.CREATE);
					algorithmId++;
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void createScenarios()
	{
		Random random = new Random(SEED);
		File[] files = MAP_DIRECTORY.listFiles();
		for (File f : files)
		{
			if (f.isFile() && f.getName().toLowerCase().endsWith(MAP_EXTENSION) && f.length() <= MAX_MAP_FILE_SIZE)
			{
				try
				{
					Graph map = mapCache.load(f);
					Nature[] natures = new Nature[]
					{ Nature.DISTANCE, Nature.TIME };
					for (int i = 0; i < natures.length; i++)
					{
						StringBuilder content = new StringBuilder();
						content.append(getMapName(f) + "\n");
						content.append(i + "\n");
						for (int j = 0; j < PATH_COUNT_PER_MAP; j++)
						{
							Scenario s = new Scenario(map, natures[i]);
							do
							{
								s.originId = random.nextInt(map.getNodes().size());
								s.destinationId = random.nextInt(map.getNodes().size());
							} while (!s.isValid());
							content.append(s.originId + " " + s.destinationId + "\n");
						}
						File file = new File(SCENARIOS_DIRECTORY.getAbsolutePath() + "/" + getScenarioFileName(f, i));
						Files.write(file.toPath(), content.toString().getBytes(), StandardOpenOption.CREATE);
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		}
	}

	private static String getMapName(File f)
	{
		return f.getName().substring(0, f.getName().length() - MAP_EXTENSION.length());
	}

	private static String getScenarioFileName(File map, int natureId)
	{
		String natureStr = new String[]
		{ "distance", "temps" }[natureId];
		return String.format("%s_%s_%d_data.txt", getMapName(map), natureStr, PATH_COUNT_PER_MAP);
	}

	private static String getScenarioResultFileName(File map, int natureId, int pathCountPerMap, String algorithmStr)
	{
		String natureStr = new String[]
		{ "distance", "temps" }[natureId];
		return String.format("%s_%s_%d_%s_test.txt", getMapName(map), natureStr, pathCountPerMap, algorithmStr);
	}
}