package controller;

import java.util.*;
import java.util.concurrent.TimeUnit;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class GameController {


	@Autowired
	private EasyRepository easyRepository;

	@Autowired
	private NormalRepository normalRepository;

	@Autowired
	private DifficultRepository difficultRepository;

	int boardSize;
	int numBlanks;
	int defaultSize = 9;
	int defaultBlanks = 60;
	int[][] fullSudoku;
	int[][] unsolvedSudoku;
	ArrayList<String> zeroes = new ArrayList<String>();
	String[] numbers;
	int numCount = 0;
	int spot = 0;
	Date start = new Date();
	Date end;
	int time;
	int[] param;
	boolean correct = true;
	String difficulty = "e";

	public GameController() {
		numBlanks = defaultBlanks;
		boardSize = defaultSize;

		GenerateSudoku sudoku = new GenerateSudoku(boardSize, defaultSize);
		sudoku.fillValues();
		fullSudoku = sudoku.getSolution();
		unsolvedSudoku = sudoku.getPuzzle();
	}

	public void resetGame(int blanks) {
		numCount = 0;
		spot = 0;
		numBlanks = blanks;
		boardSize = defaultSize;
		start = new Date();
		correct = true;
		numbers = new String[numBlanks];
		fillNumbers();

		GenerateSudoku sudoku = new GenerateSudoku(boardSize, numBlanks);
		sudoku.fillValues();
		fullSudoku = sudoku.getSolution();
		unsolvedSudoku = sudoku.getPuzzle();
		zeroes = new ArrayList<String>();
	}

	public int[][] getUnsolvedSudoku() {
		return unsolvedSudoku;
	}

	@RequestMapping(path = "/gameSubmit", method = RequestMethod.GET)
	public String getPuzzleSolution(@RequestParam(value = "param[]") int[] answers) {
		end = new Date();
		long diffInMillies = Math.abs(end.getTime() - start.getTime());
		time = (int) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		String s = getHeader();

		s += "<div class=\"card border-secondary align-items-center\" style=\"background-color:#B4BDD0\">\r\n" + "Time elapsed: " + time + " seconds."
				+ "                <div class=\"card-body\"> ";
		for (int i = 0; i < defaultSize; i += 3) {
			s += "<div class=\"row\">";
			s += getFinalSquare(3, i, 0, answers);
			s += getFinalSquare(3, i, 3, answers);
			s += getFinalSquare(3, i, 6, answers);
			s += "</div>";

		}

		if (correct) {
			s += "Add your name to the leaderboard! <form onSubmit=\"return joinLeaderboard()\"action=\"joinLeaderboard\"><input class=\"form-control\" type=\"text\" name=\"username\"><input type=\"submit\" value=\"Submit\"></form>";
		}

		s += "</div></div></body>";
		return s;
	}

	public String getSolution(int[] answers) {
		end = new Date();
		long diffInMillies = Math.abs(end.getTime() - start.getTime());
		time = (int) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		String s = getHeader();

		s += "<div class=\"card border-secondary align-items-center\" style=\"background-color:#B4BDD0\">\r\n" + "Time elapsed: " + time + " seconds."
				+ "                <div class=\"card-body\"> ";
		for (int i = 0; i < defaultSize; i += 3) {
			s += "<div class=\"row\">";
			s += getFinalSquare(3, i, 0, answers);
			s += getFinalSquare(3, i, 3, answers);
			s += getFinalSquare(3, i, 6, answers);
			s += "</div>";

		}
		return s;
	}

	public String getHeader() {
		String s = "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\">\r\n"
				+ "<link rel=\"stylesheet\" href=\"styles.css\">"
				+ "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js\"></script>\r\n"
				+ "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js\"></script>\r\n"
				+ "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js\"></script>";
		s += "<body style=\"background-color:#B4BDD0\">";
		s += "<nav class=\"nav justify-content-center\" style=\"background-color:#656199\">\r\n"
				+ "    <h3>Sudoku</h3>\r\n"
				+ "    <a class=\"nav-link\" href=\"easy\" style=\"color:black\">Easy</a>\r\n"
				+ "    <a class=\"nav-link\" href=\"normal\" style=\"color:black\">Normal</a>\r\n"
				+ "    <a class=\"nav-link\" href=\"difficult\" style=\"color:black\">Difficult</a>\r\n"
				+ "    <a class=\"nav-link\" href=\"showLeaderboard\" style=\"color:black\">Leaderboard</a>\r\n"
				+ "  </nav>";
		return s;
	}

	@RequestMapping(path = "/joinEasyLeaderboard", method = RequestMethod.GET, params = { "username" })
	public String joinEasyLeaderboard(@RequestParam("username") String username) {
		EasyWinner w = new EasyWinner();
		w.setName(username);
		w.setDifficulty(difficulty);
		w.setTime(time);
		easyRepository.save(w);
		return getLeaderboard();
	}

	@RequestMapping(path = "/joinNormalLeaderboard", method = RequestMethod.GET, params = { "username" })
	public String joinNormalLeaderboard(@RequestParam("username") String username) {
		NormalWinner w = new NormalWinner();
		w.setName(username);
		w.setDifficulty(difficulty);
		w.setTime(time);
		normalRepository.save(w);
		return getLeaderboard();
	}

	@RequestMapping(path = "/joinDifficultLeaderboard", method = RequestMethod.GET, params = { "username" })
	public String joinLeaderboard(@RequestParam("username") String username) {
		DifficultWinner w = new DifficultWinner();
		w.setName(username);
		w.setDifficulty(difficulty);
		w.setTime(time);
		difficultRepository.save(w);
		return getLeaderboard();
	}

	public @ResponseBody Iterable<EasyWinner> getAllEasyWinners() {
		return easyRepository.findAllByOrderByTimeAsc();
	}

	public @ResponseBody List<NormalWinner> getAllNormalWinners() {
		return normalRepository.findAllByOrderByTimeAsc();
	}

	public @ResponseBody List<DifficultWinner> getAllDifficultWinners() {
		return difficultRepository.findAllByOrderByTimeAsc();
	}

	public String getPuzzle(String level) {
		String s = getHeader();
		//this is where you set the background color
		s += "<div class=\"card border-secondary align-items-center\" style=\"background-color:#B4BDD0\">\r\n"
				+ "                <div class=\"card-body\"> <form onSubmit=\"return get" + level
				+ "Solution()\"action=\"" + level.toLowerCase() + "Submit\">";
		for (int i = 0; i < defaultSize; i += 3) {
			s += "<div class=\"row\" >";
			s += getSquare(3, i, 0);
			s += getSquare(3, i, 3);
			s += getSquare(3, i, 6);
			s += "</div>";

		}
		// zeroesArray = (String[]) zeroes.toArray();
		s += "<br/><button type=\"submit\" class=\"btn btn-light\">Submit</button></form></div></div></body>";
		return s;
	}

	@RequestMapping(path = "/showLeaderboard")
	public String getLeaderboard() {
		String s = getHeader();

		s += "<div class=\"card-body\" style=\"width: 18rem;\">\r\n" + "<h5 class=\"card-title\">" + "Easy<br/>"
				+ "</h5>\r\n";

		Iterator i = getAllEasyWinners().iterator();
		s += "<table class=\"table\"><tbody>";
		while (i.hasNext()) {
			// s+= "<h6 class=\"card-body\">" + i.next().toString() + "</h6>";
			s += i.next().toString();
		}
		s += " </tbody></table></div>";

		s += "<div class=\"card-body\" style=\"width: 18rem;\">\r\n" + "<h5 class=\"card-title\">" + "Normal<br/>"
				+ "</h5>\r\n";
		Iterator j = getAllNormalWinners().iterator();
		s += "<table class=\"table\"><tbody>";
		while (j.hasNext()) {
			s += j.next().toString();
		}
		s += " </tbody></table></div>";

		s += "<div class=\"card-body\" style=\"width: 18rem;\">\r\n" + "<h5 class=\"card-title\">" + "Difficult<br/>"
				+ "</h5>\r\n";
		Iterator k = getAllDifficultWinners().iterator();
		s += "<table class=\"table\"><tbody>";
		while (k.hasNext()) {
			s += k.next().toString();
		}
		s += " </tbody></table></div>";
		s += "</body>";
		return s;
	}

	public String checkform() {
		return "/gameSubmit";
	}

	public void fillNumbers() {
		for (int i = 0; i < numBlanks; i++) {
			numbers[i] = i + "";
		}
	}

	public String getSquare(int dimension, int startHorizontal, int startVertical) {
		String s = "<div class=\"card border-secondary\">\r\n"
				+ "                <div class=\"card-body bg-light\" style=\"width: 18rem;\">";
		for (int i = startHorizontal; i < startHorizontal + dimension; i++) {
			s += "<div class=\"row\">";
			for (int j = startVertical; j < startVertical + dimension; j++) {
				if (unsolvedSudoku[i][j] != 0) {
					s += " <div class=\"card-body \" style=\"background-color:#7692EO\">\r\n" + "                                <h5 class=\"card-title\">"
							+ unsolvedSudoku[i][j] + "</h5>\r\n" + "                            </div>";
				} else {
					s += " <div class=\"card-body\" style=\"background-color:#7692EO\">\r\n" + "                                <h5 class=\"card-title\">"
							+ "<input type=\"text\" style=\"width: 20px; background-color:#7b92EO\" name=\"" + "param[]" + "\">" + "</h5>\r\n"
							+ "                            </div>";
					zeroes.add(i + "" + j);
					numCount++;
				}
			}
			s += "</div>";
		}
		s += "</div></div>";
		return s;
	}

	public String getFinalSquare(int dimension, int startHorizontal, int startVertical, int[] answers) {
		String s = "<div class=\"card border-secondary\">\r\n" + "                <div class=\"card-body bg-light\" >";
		for (int i = startHorizontal; i < startHorizontal + dimension; i++) {
			s += "<div class=\"row\">";
			for (int j = startVertical; j < startVertical + dimension; j++) {
				if (unsolvedSudoku[i][j] != 0) {
					s += " <div class=\"card-body\" >\r\n" + "                                <h5 class=\"card-title\">"
							+ unsolvedSudoku[i][j] + "</h5>\r\n" + "                            </div>";
				} else {
					String stringspot = zeroes.get(spot);
					int k = Integer.parseInt(stringspot.substring(0, 1));
					int l = Integer.parseInt(stringspot.substring(1));
					if (answers[spot] == fullSudoku[k][l]) {
						s += " <div class=\"card-body\">\r\n"
								+ "                                <h5 class=\"card-title\"  style=\"background-color:#7692EO\">" + +answers[spot]
								+ "</h5>\r\n" + "                            </div>";

						spot++;
					} else {
						System.out.println("Spot = " + spot + "; answers[spot]= " + answers[spot] + "; k= " + k
								+ "; l= " + l + "; fullSudoku[k][l]= " + fullSudoku[k][l]);
						s += " <div class=\"card-body\" >\r\n"
								+ "                                <h5 class=\"card-title\"><font color=\"red\">"
								+ +answers[spot] + "</font></h5>\r\n" + "                            </div>";

						spot++;
						correct = false;
					}
				}
			}
			s += "</div>";
		}
		s += "</div></div>";
		return s;
	}

	@RequestMapping(path = "/easySubmit", method = RequestMethod.GET)
	public String getEasySolution(@RequestParam(value = "param[]") int[] answers) {
		String s = getSolution(answers);
		if (correct) {
			s += "Add your name to the leaderboard! <form onSubmit=\"return joinEasyLeaderboard()\"action=\"joinEasyLeaderboard\"><input class=\"form-control\" type=\"text\" name=\"username\"><button type=\"submit\" class=\"btn btn-light\">Submit</button></form>";
		}
		
		s += "</div></div></body>";
		return s;
	}

	@RequestMapping(path = "/normalSubmit", method = RequestMethod.GET)
	public String getNormalSolution(@RequestParam(value = "param[]") int[] answers) {
		String s = getSolution(answers);
		if (correct) {
			s += "Add your name to the leaderboard! <form onSubmit=\"return joinNormalLeaderboard()\"action=\"joinNormalLeaderboard\"><input class=\"form-control\" type=\"text\" name=\"username\"><input type=\"submit\" value=\"Submit\"></form>";
		}

		s += "</div></div></body>";
		return s;
	}

	@RequestMapping(path = "/difficultSubmit", method = RequestMethod.GET)
	public String getDifficultSolution(@RequestParam(value = "param[]") int[] answers) {
		String s = getSolution(answers);
		if (correct) {
			s += "Add your name to the leaderboard! <form onSubmit=\"return joinDifficultLeaderboard()\"action=\"joinDifficultLeaderboard\"><input class=\"form-control\" type=\"text\" name=\"username\"><input type=\"submit\" value=\"Submit\"></form>";
		}

		s += "</div></div></body>";
		return s;
	}

	@RequestMapping(path = "/easy")
	public String getEasyPuzzle() {
		resetGame(15);
		return getPuzzle("Easy");
	}

	@RequestMapping(path = "/normal")
	public String getNormalPuzzle() {
		resetGame(25);
		return getPuzzle("Normal");
	}

	@RequestMapping(path = "/difficult")
	public String getDifficultPuzzle() {
		resetGame(35);
		return getPuzzle("Difficult");
	}
}