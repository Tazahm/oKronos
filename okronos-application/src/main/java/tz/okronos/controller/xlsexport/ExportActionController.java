package tz.okronos.controller.xlsexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.Subscribe;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.controller.match.model.MatchDataVolatile;
import tz.okronos.controller.match.model.MatchDataVolatile.GoalkeeperSwap;
import tz.okronos.controller.match.model.MatchDataVolatile.Official;
import tz.okronos.controller.match.model.MatchDataVolatile.TeamData;
import tz.okronos.controller.penalty.model.PenaltyVolatile;
import tz.okronos.controller.score.model.ScoreVolatile;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.controller.xlsexport.event.request.ExportXlsRequest;
import tz.okronos.core.AbstractController;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.SimpleLateralizedPair;
import tz.okronos.core.KronoContext.ResourceType;

/**
* Builds the match report by exporting the match data into an Excel file.
* Uses an excel file as template and replaces all cells that match some predefined strings
* by their values. Example : {player.A1.name} is replaced by the name of the first player of the team A.
* See gameSheet.xslx.
 */
@Slf4j
@Configuration
public class ExportActionController extends AbstractController {

	private static String formatShirt(int shirt) {
		return String.format("%02d", shirt);
	}
	
	private static String formatTime(int time) {
		return KronoHelper.secondsToTime(time);
	}
	
	
	@Autowired private MatchDataVolatile matchData;
	@Autowired @Qualifier("playerListPropertyLateralized") 
	private SimpleLateralizedPair<ReadOnlyListProperty<PlayerSnapshot>> playerListProperties;
	@Autowired @Qualifier("penaltyHistoryListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<PenaltyVolatile>> penaltyHistoryListProperties;
	@Autowired @Qualifier("scoreListPropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyListProperty<ScoreVolatile>> scoreListProperties;
	@Autowired @Qualifier("teamNamePropertyLateralized")
    private SimpleLateralizedPair<ReadOnlyStringProperty> teamNameProperties;
	
	private Map<String, Supplier<String>> stringProviders = new HashMap<>();
	private Map<String, Supplier<Double>> doubleProviders = new HashMap<>();
	private Map<String, Supplier<Date>> dateProviders = new HashMap<>();
	private Map<String, Supplier<Boolean>> booleanProviders = new HashMap<>();

	
    @PostConstruct 
    public void init()  {
    	context.registerEventListener(this);
	}
    
    
	@Subscribe public void onExportRequest(final ExportXlsRequest request) {
		buildProviders();
		export(request.getUrl());
		clearProviders();
	}
	
	private void buildProviders() {	
		buildMatchProviders();
		
		buildPlayerProviders(playerListProperties.getLeft(), 
				p->!p.isGoalkeeper() && ! p.isOfficial(), "player", "A");
		buildPlayerProviders(playerListProperties.getLeft(), 
				p->p.isGoalkeeper(), "goalkeeper", "A");
		buildPlayerProviders(playerListProperties.getLeft(), 
				p->p.isOfficial(), "official", "A");
		
		buildPlayerProviders(playerListProperties.getRight(), 
				p->!p.isGoalkeeper() && ! p.isOfficial(), "player", "B");
		buildPlayerProviders(playerListProperties.getRight(), 
				p->p.isGoalkeeper(), "goalkeeper", "B");
		buildPlayerProviders(playerListProperties.getRight(), 
				p->p.isOfficial(), "official", "B");
		
		buildPenaltyProviders(penaltyHistoryListProperties.getLeft(), "penalty", "A");
		buildPenaltyProviders(penaltyHistoryListProperties.getRight(), "penalty", "B");

		buildScoreProviders(scoreListProperties.getLeft(), "score", "A");
		buildScoreProviders(scoreListProperties.getRight(), "score", "B");
		
		buildTeamNameProviders(teamNameProperties.getLeft(), "team", "A");
		buildTeamNameProviders(teamNameProperties.getRight(), "team", "B");				
				
		logProviders();
	}
	

	private void logProviders() {
		for (Entry<String, Supplier<String>> e : stringProviders.entrySet()) {
			log.debug("Map String '{}' to '{}'", e.getKey(), e.getValue().get());
		}
		for (Entry<String, Supplier<Double>> e : doubleProviders.entrySet()) {
			log.debug("Map Double '{}' to '{}'", e.getKey(), e.getValue().get());
		}
		for (Entry<String, Supplier<Date>> e : dateProviders.entrySet()) {
			log.debug("Map Date '{}' to '{}'", e.getKey(), e.getValue().get());
		}
		for (Entry<String, Supplier<Boolean>> e : booleanProviders.entrySet()) {
			log.debug("Map Boolean '{}' to '{}'", e.getKey(), e.getValue().get());
		}
	}
	
	private void buildMatchProviders() {
		addParam(doubleProviders, () -> (double) matchData.getMatchNumber(), "match", "number");
		addParam(stringProviders, () -> matchData.getCompetition(), "match", "championship");
		addParam(stringProviders, () -> matchData.getDate(), "match", "date");
		
		addParam(stringProviders, () -> matchData.getLocation(), "match", "location");
		addParam(stringProviders, () -> matchData.getGroup(), "match", "group");
		addParam(stringProviders, () -> formatTime(matchData.getBeginTime()), "match", "time");
		
		buildTeamProviders(matchData.getTeam().getLeft(), "A");
		buildTeamProviders(matchData.getTeam().getRight(), "B");
		
		addParam(booleanProviders, () -> matchData.isReservesBeforeMatch(), "with", "reserve");
		addParam(booleanProviders, () -> matchData.isClaim(), "with", "claim");
		addParam(booleanProviders, () -> matchData.isIncidentReport(), "with", "report");
		addParam(booleanProviders, () -> matchData.isExtension(), "with", "extension");
		
		buildRefereeOrOfficial(matchData.getReferee1(), "referee.A");
		buildRefereeOrOfficial(matchData.getReferee2(), "referee.B");
		buildRefereeOrOfficial(matchData.getMarker(), "marker");
		buildRefereeOrOfficial(matchData.getChrono(), "chrono");		
	}
	
	private void buildRefereeOrOfficial(Official official, String prefix) {
		addParam(stringProviders, () -> official.getLicence(), prefix, "licence");
		addParam(stringProviders, () -> official.getName(), prefix, "name");
	}
	
	private void buildTeamProviders(TeamData team, String prefix) {
		buildGoalkeeperSwap(team.getGoalkeeperSwaps(0), prefix + "1");
		buildGoalkeeperSwap(team.getGoalkeeperSwaps(1), prefix + "2");
		buildGoalkeeperSwap(team.getGoalkeeperSwaps(2), prefix + "3");
		
		addParam(stringProviders, () ->formatTime(team.getTimeoutPeriod1()), "timeout", prefix + "1");
		addParam(stringProviders, () ->formatTime(team.getTimeoutPeriod2()), "timeout", prefix + "2");
	}


	private void buildGoalkeeperSwap(GoalkeeperSwap goalkeeperSwaps, String prefix) {
		addParam(stringProviders, () -> formatShirt(goalkeeperSwaps.getSheet()), "goalswap" + "." + prefix, "shirt");
		addParam(stringProviders, () -> formatTime(goalkeeperSwaps.getTime()), "goalswap" + "." + prefix, "time");
	}


	private void buildPlayerProviders(final List<PlayerSnapshot> players,
			final Predicate<PlayerSnapshot> filter,
			final String prefix1, final String prefix2) {
		final List<PlayerSnapshot> filtered = players.stream().filter(filter)
			.collect(Collectors.toList());	
		for (int i = 0; i < filtered.size(); i++) {
			final PlayerSnapshot player = filtered.get(i);
			addParam(stringProviders, () -> player.getLicence(), prefix1, prefix2, i, "licence");
			addParam(stringProviders, () -> player.getName(), prefix1, prefix2, i, "name");
			addParam(stringProviders, () -> formatShirt(player.getShirt()), prefix1, prefix2, i, "shirt");
		}
	}
	
	private void buildPenaltyProviders(final List<PenaltyVolatile> penaltyVolatiles, final String prefix1, String prefix2) {
		for (int i = 0; i < penaltyVolatiles.size(); i++) {
			final PenaltyVolatile penaltyVolatile = penaltyVolatiles.get(i);
			addParam(stringProviders, () -> formatTime(penaltyVolatile.getPenaltyTime()), prefix1, prefix2, i, "time");
			addParam(doubleProviders, () -> (double) penaltyVolatile.getPlayer(), prefix1, prefix2, i, "number");
			addParam(stringProviders, () -> penaltyVolatile.getCode(), prefix1, prefix2, i, "code");
			addParam(doubleProviders, () -> (double) penaltyVolatile.getDuration(), prefix1, prefix2, i, "duration");
			if (penaltyVolatile.getStartTime() >= 0) {
			    addParam(stringProviders, () -> formatTime(penaltyVolatile.getStartTime()), prefix1, prefix2, i, "start");
			}
			if (penaltyVolatile.getStopTime() >= 0) {
				addParam(stringProviders, () -> formatTime(penaltyVolatile.getStopTime()), prefix1, prefix2, i, "stop");
			}
		}
		
		String prefix = prefix1 + "." + prefix2;
		addParam(doubleProviders, () -> (double) penaltyVolatiles.size(), prefix, "total");
		addParam(doubleProviders, () -> (double) penaltyVolatiles.stream().filter(p -> p.getPeriod() == 1).count(), prefix, "halftime1");
		addParam(doubleProviders, () -> (double) penaltyVolatiles.stream().filter(p -> p.getPeriod() == 2).count(), prefix, "halftime2");
		addParam(doubleProviders, () -> (double) penaltyVolatiles.stream().filter(p -> p.getPeriod() == 3).count(), prefix, "extension");
	}

	private void buildScoreProviders(final List<ScoreVolatile> scoreVolatiles, final String prefix1, String prefix2) {
		for (int i = 0; i < scoreVolatiles.size(); i++) {
			final ScoreVolatile scoreVolatile = scoreVolatiles.get(i);
			addParam(stringProviders, () -> formatTime(scoreVolatile.getTime()), prefix1, prefix2, i, "time");
			if (scoreVolatile.getScorer() >= 0) {
				addParam(stringProviders, () ->  formatShirt(scoreVolatile.getScorer()), prefix1, prefix2, i, "scorer");
			}
			if (scoreVolatile.getAssist1() >= 0) {
				addParam(stringProviders, () ->  formatShirt(scoreVolatile.getAssist1()), prefix1, prefix2, i, "assist1");
			}
			if ( scoreVolatile.getAssist2() >= 0) {
				addParam(stringProviders, () ->  formatShirt(scoreVolatile.getAssist2()), prefix1, prefix2, i, "assist2");
			}
		}
		
		String prefix = prefix1 + "." + prefix2;
		addParam(doubleProviders, () -> (double) scoreVolatiles.size(), prefix, "total");		
		addParam(doubleProviders, () -> (double) scoreVolatiles.stream().filter(m -> m.getPeriod() == 1).count(), prefix, "halftime1");
		addParam(doubleProviders, () -> (double) scoreVolatiles.stream().filter(m -> m.getPeriod() == 2).count(), prefix, "halftime2");
		addParam(doubleProviders, () -> (double) scoreVolatiles.stream().filter(m -> m.getPeriod() == 3).count(), prefix, "extension");
		addParam(doubleProviders, () -> (double) scoreVolatiles.stream().filter(m -> m.getPeriod() == 4).count(), prefix, "shoots");
	}

	private void buildTeamNameProviders(ReadOnlyStringProperty property, String prefix1, String prefix2) {
		addParam(stringProviders, () -> property.get(), prefix1, prefix2);
	}


	private <T> void addParam(Map<String, Supplier<T>> map, Supplier<T> supplier, String prefix1, String prefix2, int index, String suffix) {
		addParam(map, supplier, prefix1 + "." + prefix2 + (index + 1), suffix);
	}

	private <T> void addParam(Map<String, Supplier<T>> map, Supplier<T> supplier, String prefix, String suffix) {
		map.put(prefix + "." + suffix, supplier);
	}

	private void clearProviders() {
		stringProviders.clear();
		doubleProviders.clear();
		dateProviders.clear();
		booleanProviders.clear();
	}
	
	private void export(final String url) {
		File template = context.getLocalFile("gameSheet.xlsx", ResourceType.CONFIG);
		if (template == null) {
			log.error("Cannot find the game sheet template");
			return;
		}
		File output = KronoHelper.urlToNewFile(url);
		if (output == null) {
			log.error("Invalid url");
			return;
		}
		File work = new File(output.getParent(), "gameSheet.tmp");
		
		try {
			Files.copy(template.toPath(), work.toPath());
		} catch (IOException e) {
			log.error("Cannot copy");
			return;
		}
		
		try (OPCPackage pkg = OPCPackage.open(work)) {
			XSSFWorkbook wb = new XSSFWorkbook(pkg);
			parseWorkbook(wb);
			try (OutputStream fileOut = new FileOutputStream(output)) {
			    wb.write(fileOut);
			}
		}
		catch (IOException | InvalidFormatException ex) {
			log.error(ex.getMessage(), ex);
		}
		
		work.delete();
	}
	
	private void parseWorkbook(final XSSFWorkbook workbook) {
		for (Sheet sheet : workbook) {
			//log.debug("Sheet {}", sheet.getSheetName());
			for (Row row : sheet) {
				//log.debug("Row {}", row.getRowNum());
				for (Cell cell : row) {
					//log.debug("Cell type {}", cell.getCellType());
					if (Cell.CELL_TYPE_STRING == cell.getCellType()) {						
						String value = cell.getStringCellValue();
						//log.debug("Cell value {}", cell.getStringCellValue());
						if (value.startsWith("{") && value.endsWith("}")) {
							replaceCellValue(cell, value);
						}
					}
				}
			}
		}
		
	}
	
	private void replaceCellValue(Cell cell,  String cellValue) {
		String key = cellValue.substring(1, cellValue.length() - 1);
		log.debug("Handle {}", key);
		boolean res = replaceString(cell, key) 
			|| replaceDouble(cell, key) 
			|| replaceDate(cell, key) 
			|| replaceBoolean(cell, key) ;
		if (! res) {
			log.debug("No mapping for {}", key);
			cell.setCellType(Cell.CELL_TYPE_BLANK);
		}
	}
	
	private boolean replaceString(Cell cell, String key) {
		Supplier<String> provider = stringProviders.get(key);
		if (provider == null) return false;
		String value = provider.get();
		log.debug("Replace String '{}' by '{}'", key, value);
		cell.setCellValue(value);
		return true;
	}
	
	private boolean replaceDouble(Cell cell, String key) {
		Supplier<Double> provider = doubleProviders.get(key);
		if (provider == null) return false;
		Double value = provider.get();
		log.debug("Replace Double '{}' by '{}'", key, value);
		cell.setCellValue(value);
		return true;
	}
	
	private boolean replaceDate(Cell cell, String key) {
		Supplier<Date> provider = dateProviders.get(key);
		if (provider == null) return false;
		Date value = provider.get();
		log.debug("Replace Date '{}' by '{}'", key, value);
		cell.setCellValue(value);
		return true;
	}
	
	private boolean replaceBoolean(Cell cell, String key) {
		Supplier<Boolean> provider = booleanProviders.get(key);
		if (provider == null) return false;
		Boolean value = provider.get();
		log.debug("Replace Boolean '{}' by '{}'", key, value);
		cell.setCellValue(value ? "oui" : "non");
		return true;
	}
}
