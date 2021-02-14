package tz.okronos.controller.team;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.eventbus.Subscribe;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tz.okronos.annotation.twosidebeans.TwoSideBean;
import tz.okronos.annotation.twosidebeans.TwoSideConfiguration;
import tz.okronos.annotation.twosidebeans.TwoSidePostConstruct;
import tz.okronos.controller.report.event.notif.ReportBuildAnswer;
import tz.okronos.controller.report.event.request.ReportBuildRequest;
import tz.okronos.controller.report.event.request.ReportReinitRequest;
import tz.okronos.controller.team.event.notif.TeamPlayerCreationNotif;
import tz.okronos.controller.team.event.notif.TeamPlayerDeletionNotif;
import tz.okronos.controller.team.event.notif.TeamPlayerModificationNotif;
import tz.okronos.controller.team.event.request.TeamExportRequest;
import tz.okronos.controller.team.event.request.TeamImageModificationRequest;
import tz.okronos.controller.team.event.request.TeamImportRequest;
import tz.okronos.controller.team.event.request.TeamNameModificationRequest;
import tz.okronos.controller.team.event.request.TeamPlayerCreationRequest;
import tz.okronos.controller.team.event.request.TeamPlayerDeletionRequest;
import tz.okronos.controller.team.event.request.TeamPlayerModificationRequest;
import tz.okronos.controller.team.model.PlayerSnapshot;
import tz.okronos.controller.team.model.TeamModel;
import tz.okronos.controller.team.model.TeamReport;
import tz.okronos.core.AbstractModelController;
import tz.okronos.core.KronoHelper;
import tz.okronos.core.Lateralized;
import tz.okronos.core.PlayPosition;
import tz.okronos.core.SideAware;
import tz.okronos.event.request.ResetPlayRequest;


/**
 *  Handles data related to a a team : team name and image, players.
 */
@TwoSideConfiguration
@Slf4j
public class TeamModelController 
	extends AbstractModelController<TeamModel>
	implements Lateralized, SideAware {
	
	public static final String ReportIdPreffix = "team";
	private TeamModel model;
	
	@Getter private PlayPosition side;
	private String reportId = ReportIdPreffix;
	
	
	@Bean @Scope("prototype")
	protected TeamModel teamModel() {
		return new TeamModel();
	}
	
	
	public TeamModelController() {		
	}
	 
	@TwoSidePostConstruct
	public void init() {
		model = teamModel();
		resetTeamName();
		context.registerEventListener(this);
	}

	@TwoSideBean
	public ReadOnlyStringProperty teamNameProperty() {
		return model.getTeamNameWrapper().getReadOnlyProperty();
	}	
	
	@TwoSideBean
	public ReadOnlyObjectProperty<Image> teamImageProperty() {
		return model.getTeamImageWrapper().getReadOnlyProperty();
	}
	
	@TwoSideBean
    public ReadOnlyListProperty<PlayerSnapshot> playerListProperty() {
    	return model.getPlayerSortedListWrapper().getReadOnlyProperty();
    }

  	@Subscribe public void onResetPlayRequest(ResetPlayRequest request) {
  		reset();
  	}
	
	@Subscribe public void onTeamNameModificationRequest(TeamNameModificationRequest request) {
		if (side != request.getSide()) return;
		model.getTeamNameWrapper().set(request.getTeamName());
	}
	
	@Subscribe public void onTeamImageModificationRequest(TeamImageModificationRequest request) {
		if (side != request.getSide()) return;
		model.getTeamImageWrapper().set(request.getImage());
	}

	@Subscribe public void onTeamPlayerCreationRequest(TeamPlayerCreationRequest request) {
		if (side != request.getSide()) return;
		addPlayer(request.getPlayer());
	}
	
	@Subscribe public void onTeamPlayerModificationRequest(TeamPlayerModificationRequest request) {
		if (side != request.getSide()) return;
		modifyPlayer(request.getPlayer());
	}
	
	@Subscribe public void onTeamPlayerDeletionRequest(TeamPlayerDeletionRequest request) {
		if (side != request.getSide()) return;
		deletePlayer(request.getPlayer());
	}
	
	@Subscribe public void onTeamImportRequest(TeamImportRequest request) {
		if (side != request.getSide()) return;
		importPlayer(request);
	}
	
	@Subscribe public void onTeamExportRequest(TeamExportRequest request) {
		if (side != request.getSide()) return;
		exportPlayer(request);
	}
	
	@Subscribe public void onReportBuildRequest(ReportBuildRequest request) {
  		buildReport(request);
  	}

	@Subscribe public void onReportReinitRequest(ReportReinitRequest request) {
		if (! reportId.equals(request.getCategoryId())) return;
		reinit(request);
  	}

	@Override
	public void setSide(PlayPosition position) {
		this.side = position;
		reportId = reportId + "-" + side.toString().toLowerCase();
	}

	private void buildReport(ReportBuildRequest request) {
		Image image = model.getTeamImageWrapper().get();
		PlayerSnapshot[] players = KronoHelper.toArray(model.getPlayerListWrapper(), Function.identity(), 
				Comparator.comparing(PlayerSnapshot::getUid), PlayerSnapshot.class);
		
		final TeamReport content = new TeamReport();
		content.setImageUrl(image == null ? null : image.getUrl());
		content.setTeamName(model.getTeamNameWrapper().get());
		content.setPlayerList(players);
		
		context.postEvent(new ReportBuildAnswer()
			.setCategoryId(reportId)
			.setContent(content)
			.setRequestId(request.getRequestId()));
	}

	private void reinit(ReportReinitRequest request) {
		TeamReport snapshot = (TeamReport) request.getContent();
		
		try {
			String url = snapshot.getImageUrl() ; 
			if (url != null && ! url.isEmpty()) {
				File imageFile = KronoHelper.urlToFile(url);
				Image image = new Image(new FileInputStream(imageFile));
				model.getTeamImageWrapper().set(image);
			}
		} catch (FileNotFoundException ex) {
			log.error(ex.getMessage());
		}
		
		model.getTeamNameWrapper().set(snapshot.getTeamName());
		
		List<PlayerSnapshot> players = KronoHelper.fromArray(snapshot.getPlayerList(), Function.identity());
		model.getPlayerListWrapper().clear();
		model.getPlayerListWrapper().addAll(players);		
	}
	
	private void reset() {
		resetTeamName();
		model.getPlayerListWrapper().clear();
	}
	
	
	private void resetTeamName() {
		String teamProp = side == PlayPosition.LEFT ? "leftTeamName" : "rightTeamName";
		String defaultName = side == PlayPosition.LEFT ? "Locaux" : "Visiteurs";
		String teamName = context.getProperty(teamProp, defaultName);
		model.getTeamNameWrapper().set(teamName);
	}

	private void addPlayer(PlayerSnapshot input) {
		PlayerSnapshot player = PlayerSnapshot.of(input);
		player.generateUID();
		input.setTeam(side);
		model.getPlayerListWrapper().add(player);
		context.postEvent(new TeamPlayerCreationNotif()
			.setPlayer(PlayerSnapshot.of(player)).setSide(side));
	}

	private void modifyPlayer(PlayerSnapshot input) {
		PlayerSnapshot player = model.getPlayerListWrapper().stream()
			.filter(p -> p.getUid() == input.getUid()).findFirst().orElse(null);
		if (player == null) return;
		player.copy(input);
		
		// Force ordering recomputation.
		model.getPlayerListWrapper().remove(player);
		model.getPlayerListWrapper().add(player);
		
		context.postEvent(new TeamPlayerModificationNotif()
			.setPlayer(PlayerSnapshot.of(player)).setSide(side));
	}

	private void deletePlayer(PlayerSnapshot input) {
		PlayerSnapshot player = model.getPlayerListWrapper().stream()
				.filter(p -> p.getUid() == input.getUid()).findFirst().orElse(null);
		if (player == null) return;
		model.getPlayerListWrapper().remove(player);
        context.postEvent(new TeamPlayerDeletionNotif()
        	.setPlayer(PlayerSnapshot.of(player)).setSide(side));
	}

	private void importPlayer(TeamImportRequest request) {
		try {
			File src = new File(new URL(request.getUrl()).getFile());
			List<PlayerSnapshot> players = readPlayers(src);
			model.getPlayerListWrapper().clear();
			model.getPlayerListWrapper().addAll(players);
		} catch (IOException ex) {
			log.warn(ex.getMessage());
		}
	}
	
	private void exportPlayer(TeamExportRequest request) {
		try {
			File dst = new File(new URL(request.getUrl()).getFile());
			writePlayers(dst);
		} catch (IOException ex) {
			log.warn(ex.getMessage());
		}
	}
 
	private void writePlayers(File dst) throws IOException {
		  CsvMapper mapper = new CsvMapper();
		  mapper.configure(com.fasterxml.jackson.core.JsonGenerator.Feature.IGNORE_UNKNOWN, true);
		  CsvSchema schema = csvPlayerBuilder();
		  ObjectWriter writer = mapper.writerFor(PlayerSnapshot.class).with(schema);
		  writer.writeValues(dst).writeAll(model.getPlayerSortedListWrapper());
	}

	 private List<PlayerSnapshot> readPlayers(File src) throws FileNotFoundException, IOException {
		  List<PlayerSnapshot> players = new ArrayList<>();
		  CsvSchema schema = csvPlayerBuilder();
		  CsvMapper mapper = new CsvMapper();
		  ObjectReader objectReader = mapper.readerFor(PlayerSnapshot.class).with(schema);
		  try (Reader reader = new FileReader(src)) {
		     MappingIterator<PlayerSnapshot> mi = objectReader.readValues(reader);
		     while (mi.hasNext()) {
		    	 PlayerSnapshot player = mi.next();
		    	 player.generateUID();
		    	 player.setTeam(side);
		    	 players.add(player);
		     }
		  }
		  return players;
	 }
 
	private CsvSchema csvPlayerBuilder() {
		return CsvSchema.builder()
		      .addColumn("name")
		      .addColumn("licence")
		      .addColumn("shirt")
		      .addColumn("goalkeeper")
		      .addColumn("official")
		      .build();
	}


	@Override
	protected TeamModel getModel() {
		return model;
	}


	

}


