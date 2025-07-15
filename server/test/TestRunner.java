import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("=== ViaBook Server Test Runner ===");
        System.out.println("Running Server Unit and System Tests...\n");
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        System.out.println("--- Running Server Unit Tests ---");
        runTestClass(launcher, listener, "test.UserTest");
        System.out.println("\n--- Running Server System Tests ---");
        runTestClass(launcher, listener, "test.DatabaseWorkflowTest");
        printTestSummary(listener.getSummary());
    }
    private static void runTestClass(Launcher launcher, SummaryGeneratingListener listener, String className) {
        try {
            Class<?> testClass = Class.forName(className);
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectClass(testClass))
                .build();
            launcher.execute(request, listener);
            System.out.println("✅ " + className + " completed");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ " + className + " not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error running " + className + ": " + e.getMessage());
        }
    }
    private static void printTestSummary(TestExecutionSummary summary) {
        System.out.println("\n=== Test Execution Summary ===");
        System.out.println("Tests found: " + summary.getTestsFoundCount());
        System.out.println("Tests successful: " + summary.getTestsSucceededCount());
        System.out.println("Tests failed: " + summary.getTestsFailedCount());
        System.out.println("Tests skipped: " + summary.getTestsSkippedCount());
        System.out.println("Total time: " + summary.getTotalTime().toMillis() + "ms");
        if (summary.getTestsFailedCount() > 0) {
            System.out.println("\n=== Failed Tests ===");
            summary.getFailures().forEach(failure -> {
                System.out.println("- " + failure.getTestIdentifier().getDisplayName());
                System.out.println("  Error: " + failure.getException().getMessage());
            });
        }
        if (summary.getTestsSucceededCount() == summary.getTestsFoundCount()) {
            System.out.println("\n🎉 All tests passed successfully!");
        } else {
            System.out.println("\n⚠️  Some tests failed. Please check the output above.");
        }
    }
} 