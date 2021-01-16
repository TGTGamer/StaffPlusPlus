package net.shortninja.staffplus.staff.reporting;

import net.shortninja.staffplus.IocContainer;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.common.exceptions.BusinessException;
import net.shortninja.staffplus.common.exceptions.NoPermissionException;
import net.shortninja.staffplus.event.*;
import net.shortninja.staffplus.player.SppPlayer;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.staff.reporting.database.ReportRepository;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.PermissionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getScheduler;

public class ManageReportService {

    private static final Map<UUID, Long> lastUse = new HashMap<UUID, Long>();

    private final PermissionHandler permission = IocContainer.getPermissionHandler();
    private final MessageCoordinator message = IocContainer.getMessage();
    private final Options options = IocContainer.getOptions();
    private final Messages messages;
    private final ReportService reportService;
    private final ReportRepository reportRepository;

    public ManageReportService(ReportRepository reportRepository, Messages messages, ReportService reportService) {
        this.reportRepository = reportRepository;
        this.messages = messages;
        this.reportService = reportService;
    }

    public void clearReports(SppPlayer player) {
        reportRepository.removeReports(player.getId());
    }

    public void acceptReport(Player player, int reportId) {
        if(!permission.has(player, options.manageReportConfiguration.getPermissionReject())) {
            throw new NoPermissionException();
        }

        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            Report report = reportRepository.findOpenReport(reportId)
                .orElseThrow(() -> new BusinessException("Report with id [" + reportId + "] not found", messages.prefixReports));

            report.setReportStatus(ReportStatus.IN_PROGRESS);
            report.setStaffUuid(player.getUniqueId());
            report.setStaffName(player.getName());
            reportRepository.updateReport(report);
            message.sendGroupMessage(player.getName() + " accepted report from " + report.getReporterName(), options.permissionReportUpdateNotifications, messages.prefixReports);
            sendEvent(new AcceptReportEvent(report));
        });

    }

    public void reopenReport(Player player, int reportId) {
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            Report report = reportService.getReport(reportId);
            if (!report.getStaffUuid().equals(player.getUniqueId())) {
                throw new BusinessException("&CYou cannot change the status of a report you are not assigned to", messages.prefixReports);
            }

            report.setStaffUuid(null);
            report.setStaffName(null);
            report.setReportStatus(ReportStatus.OPEN);
            reportRepository.updateReport(report);
            message.sendGroupMessage(player.getName() + " reopened report from " + report.getReporterName(), options.permissionReportUpdateNotifications, messages.prefixReports);
            sendEvent(new ReopenReportEvent(report));
        });
    }

    public void closeReport(Player player, CloseReportRequest closeReportRequest) {
        getScheduler().runTaskAsynchronously(StaffPlus.get(), () -> {
            Report report = reportService.getReport(closeReportRequest.getReportId());
            closedReport(player, report, closeReportRequest.getStatus(), closeReportRequest.getCloseReason());
            message.sendGroupMessage(player.getName() + " changed report status to " + closeReportRequest.getStatus() + ". Reporter: " + report.getReporterName(), options.permissionReportUpdateNotifications, messages.prefixReports);
            if (closeReportRequest.getStatus() == ReportStatus.REJECTED) {
                sendEvent(new RejectReportEvent(report));
            } else {
                sendEvent(new ResolveReportEvent(report));
            }
        });
    }

    private void closedReport(Player player, Report report, ReportStatus status, String closeReason) {
        if (!report.getStaffUuid().equals(player.getUniqueId())) {
            throw new BusinessException("&CYou cannot change the status of a report you are not assigned to", messages.prefixReports);
        }

        if(status == ReportStatus.REJECTED && !permission.has(player, options.manageReportConfiguration.getPermissionReject())) {
            throw new NoPermissionException();
        }
        if(status == ReportStatus.RESOLVED && !permission.has(player, options.manageReportConfiguration.getPermissionResolve())) {
            throw new NoPermissionException();
        }
        report.setReportStatus(status);
        report.setCloseReason(closeReason);
        reportRepository.updateReport(report);
    }

    public List<Report> getClosedReports(int offset, int amount) {
        return reportRepository.getClosedReports(offset, amount);
    }

    private void sendEvent(Event event) {
        getScheduler().runTask(StaffPlus.get(), () -> {
            Bukkit.getPluginManager().callEvent(event);
        });
    }

    public void deleteReport(Player player, int reportId) {
        if(!permission.has(player, options.manageReportConfiguration.getPermissionDelete())) {
            throw new NoPermissionException();
        }
        Report report = reportService.getReport(reportId);
        reportRepository.markReportDeleted(report);
        message.sendGroupMessage(player.getName() + " deleted report from " + report.getReporterName(), options.permissionReportUpdateNotifications, messages.prefixReports);
    }
}
