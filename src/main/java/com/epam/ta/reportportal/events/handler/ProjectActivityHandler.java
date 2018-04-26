/*
 * Copyright 2016 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/reportportal/service-api
 *
 * Report Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Report Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.epam.ta.reportportal.events.handler;

import com.epam.ta.reportportal.database.dao.ActivityRepository;
import com.epam.ta.reportportal.database.entity.AnalyzeMode;
import com.epam.ta.reportportal.database.entity.Project;
import com.epam.ta.reportportal.database.entity.item.Activity;
import com.epam.ta.reportportal.events.ProjectUpdatedEvent;
import com.epam.ta.reportportal.ws.converter.builders.ActivityBuilder;
import com.epam.ta.reportportal.ws.model.project.AnalyzerConfig;
import com.epam.ta.reportportal.ws.model.project.ProjectConfiguration;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.ta.reportportal.database.entity.item.ActivityEventType.UPDATE_PROJECT;
import static com.epam.ta.reportportal.database.entity.item.ActivityObjectType.PROJECT;
import static com.epam.ta.reportportal.events.handler.EventHandlerUtil.createHistoryField;

/**
 * Saves new project activity
 *
 * @author Andrei Varabyeu
 */
@Component
public class ProjectActivityHandler {

	static final String KEEP_SCREENSHOTS = "keepScreenshots";
	static final String KEEP_LOGS = "keepLogs";
	static final String LAUNCH_INACTIVITY = "launchInactivity";
	static final String STATISTICS_CALCULATION_STRATEGY = "statisticsCalculationStrategy";
	static final String AUTO_ANALYZE = "auto_analyze";
	static final String ANALYZE_MODE = "analyze_mode";
	static final String MIN_DOC_FREQ = "min_doc_freq";
	static final String MIN_TERM_FREQ = "min_term_freq";
	static final String MIN_SHOULD_MATCH = "min_should_match";
	static final String NUMBER_OF_LOG_LINES = "number_of_log_lines";

	private final ActivityRepository activityRepository;

	@Autowired
	public ProjectActivityHandler(ActivityRepository activityRepository) {
		this.activityRepository = activityRepository;
	}

	@EventListener
	public void onApplicationEvent(ProjectUpdatedEvent event) {
		Project project = event.getBefore();
		List<Activity.FieldValues> history = Lists.newArrayList();

		ProjectConfiguration configuration = event.getUpdateProjectRQ().getConfiguration();
		if (null != configuration) {
			processKeepLogs(history, project, configuration);
			processKeepScreenshots(history, project, configuration);
			processLaunchInactivityTimeout(history, project, configuration);
			processAutoAnalyze(history, project, configuration);
			processAnalyzerConfig(history, project, configuration.getAnalyzerConfig());
			processStatisticsStrategy(history, project, configuration);
		}

		if (!history.isEmpty()) {
			Activity activityLog = new ActivityBuilder().addProjectRef(project.getName())
					.addObjectType(PROJECT)
					.addObjectName(project.getName())
					.addActionType(UPDATE_PROJECT)
					.addUserRef(event.getUpdatedBy())
					.addHistory(history.isEmpty() ? null : history)
					.get();
			activityRepository.save(activityLog);
		}
	}

	private void processAnalyzerConfig(List<Activity.FieldValues> history, Project project, AnalyzerConfig analyzerConfig) {
		processAnalyzeMode(history, project, AnalyzeMode.fromString(analyzerConfig.getAnalyzerMode()));
		processElasticParameters(
				history, MIN_DOC_FREQ, project.getConfiguration().getAnalyzerConfig().getMinDocFreq(), analyzerConfig.getMinDocFreq());
		processElasticParameters(
				history, MIN_TERM_FREQ, project.getConfiguration().getAnalyzerConfig().getMinTermFreq(), analyzerConfig.getMinTermFreq());
		processElasticParameters(history, MIN_SHOULD_MATCH, project.getConfiguration().getAnalyzerConfig().getMinShouldMatch(),
				analyzerConfig.getMinShouldMatch()
		);
		processElasticParameters(history, NUMBER_OF_LOG_LINES, project.getConfiguration().getAnalyzerConfig().getNumberOfLogLines(),
				analyzerConfig.getNumberOfLogLines()
		);
	}

	private void processElasticParameters(List<Activity.FieldValues> history, String elasticParameterName, int oldValue, int newValue) {
		if (oldValue != newValue) {
			history.add(createHistoryField(elasticParameterName, String.valueOf(oldValue), String.valueOf(newValue)));
		}
	}

	private void processAnalyzeMode(List<Activity.FieldValues> history, Project project, AnalyzeMode mode) {
		AnalyzeMode oldMode = project.getConfiguration().getAnalyzerConfig().getAnalyzerMode();
		if (null != mode && mode != oldMode) {
			history.add(createHistoryField(ANALYZE_MODE, oldMode != null ? oldMode.getValue() : "", mode.getValue()));
		}
	}

	private void processStatisticsStrategy(List<Activity.FieldValues> history, Project project, ProjectConfiguration configuration) {
		if ((null != configuration.getStatisticCalculationStrategy()) && (!configuration.getStatisticCalculationStrategy()
				.equalsIgnoreCase((project.getConfiguration().getStatisticsCalculationStrategy().name())))) {
			Activity.FieldValues fieldValues = createHistoryField(STATISTICS_CALCULATION_STRATEGY,
					project.getConfiguration().getStatisticsCalculationStrategy().name(), configuration.getStatisticCalculationStrategy()
			);
			history.add(fieldValues);
		}
	}

	private void processKeepLogs(List<Activity.FieldValues> history, Project project, ProjectConfiguration configuration) {
		if ((null != configuration.getKeepLogs()) && (!configuration.getKeepLogs().equals(project.getConfiguration().getKeepLogs()))) {
			Activity.FieldValues fieldValues = createHistoryField(
					KEEP_LOGS, project.getConfiguration().getKeepLogs(), configuration.getKeepLogs());
			history.add(fieldValues);
		}
	}

	private void processLaunchInactivityTimeout(List<Activity.FieldValues> history, Project project, ProjectConfiguration configuration) {
		if ((null != configuration.getInterruptJobTime()) && (!configuration.getInterruptJobTime()
				.equals(project.getConfiguration().getInterruptJobTime()))) {
			Activity.FieldValues fieldValues = createHistoryField(
					LAUNCH_INACTIVITY, project.getConfiguration().getInterruptJobTime(), configuration.getInterruptJobTime());
			history.add(fieldValues);
		}
	}

	private void processAutoAnalyze(List<Activity.FieldValues> history, Project project, ProjectConfiguration configuration) {
		if (null != configuration.getAnalyzerConfig().getIsAutoAnalyzerEnabled() && !configuration.getAnalyzerConfig()
				.getIsAutoAnalyzerEnabled()
				.equals(project.getConfiguration().getAnalyzerConfig().getIsAutoAnalyzerEnabled())) {
			String oldValue = project.getConfiguration().getAnalyzerConfig().getIsAutoAnalyzerEnabled() == null ?
					"" :
					project.getConfiguration().getAnalyzerConfig().getIsAutoAnalyzerEnabled().toString();
			Activity.FieldValues fieldValues = createHistoryField(
					AUTO_ANALYZE, oldValue, configuration.getAnalyzerConfig().getIsAutoAnalyzerEnabled().toString());
			history.add(fieldValues);
		}
	}

	private void processKeepScreenshots(List<Activity.FieldValues> history, Project project, ProjectConfiguration configuration) {
		if ((null != configuration.getKeepScreenshots()) && (!configuration.getKeepScreenshots()
				.equals(project.getConfiguration().getKeepScreenshots()))) {
			Activity.FieldValues fieldValues = createHistoryField(
					KEEP_SCREENSHOTS, project.getConfiguration().getKeepScreenshots(), configuration.getKeepScreenshots());
			history.add(fieldValues);
		}
	}
}
