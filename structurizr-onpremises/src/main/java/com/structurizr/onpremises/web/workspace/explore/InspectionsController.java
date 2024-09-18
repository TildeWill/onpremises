package com.structurizr.onpremises.web.workspace.explore;

import com.structurizr.Workspace;
import com.structurizr.inspection.DefaultInspector;
import com.structurizr.inspection.Inspector;
import com.structurizr.inspection.Severity;
import com.structurizr.inspection.Violation;
import com.structurizr.onpremises.component.workspace.WorkspaceMetaData;
import com.structurizr.onpremises.web.workspace.AbstractWorkspaceController;
import com.structurizr.util.WorkspaceUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
public class InspectionsController extends AbstractWorkspaceController {

    private static final Log log = LogFactory.getLog(InspectionsController.class);
    private static final String VIEW = "inspections";

    @RequestMapping(value = "/workspace/{workspaceId}/inspections", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String showInspections(
            @PathVariable("workspaceId") long workspaceId,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String version,
            ModelMap model
    ) {
        WorkspaceMetaData workspaceMetaData = workspaceComponent.getWorkspaceMetaData(workspaceId);
        if (workspaceMetaData == null) {
            return show404Page(model);
        }

        if (workspaceMetaData.isClientEncrypted()) {
            return showError("workspace-is-client-side-encrypted", model);
        }

        if (userCanAccessWorkspace(workspaceMetaData)) {
            try {
                String json = workspaceComponent.getWorkspace(workspaceMetaData.getId(), branch, version);
                Workspace workspace = WorkspaceUtils.fromJson(json);
                Inspector inspector = new DefaultInspector(workspace);
                List<Violation> violations = inspector.getViolations();
                violations.sort(Comparator.comparing(Violation::getSeverity));
                model.addAttribute("violations", violations);
                model.addAttribute("numberOfInspections", inspector.getNumberOfInspections());
                model.addAttribute("numberOfViolations", violations.size());
                model.addAttribute("numberOfErrors", violations.stream().filter(r -> r.getSeverity() == Severity.ERROR).count());
                model.addAttribute("numberOfWarnings", violations.stream().filter(r -> r.getSeverity() == Severity.WARNING).count());
                model.addAttribute("numberOfInfos", violations.stream().filter(r -> r.getSeverity() == Severity.INFO).count());
                model.addAttribute("numberOfIgnores", violations.stream().filter(r -> r.getSeverity() == Severity.IGNORE).count());

                return showAuthenticatedView(VIEW, workspaceMetaData, branch, version, model, true, false);
            } catch (Exception e) {
                log.error(e);
                throw new RuntimeException(e);
            }
        }

        return show404Page(model);
    }

}