package com.intuit.innersource.reposcanner.cli;

import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

public class VersionProvider implements IVersionProvider {

    @Spec
    private CommandSpec spec;

    @Override
    public String[] getVersion() {
        final BuildInfoService buildInfo = BuildInfoService.getInstance();
        return new String[] {
            String.format(
                "%s %s (%s; %s)",
                spec.qualifiedName(),
                buildInfo.getProperty("application.version"),
                buildInfo.getProperty("application.build.commit"),
                buildInfo.getProperty("application.build.time")
            ),
        };
    }
}
