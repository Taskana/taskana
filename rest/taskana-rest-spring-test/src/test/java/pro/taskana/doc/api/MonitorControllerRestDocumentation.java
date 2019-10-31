package pro.taskana.doc.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Generate REST docu for the monitor controller.
 *
 */
public class MonitorControllerRestDocumentation extends BaseRestDocumentation {

    private FieldDescriptor[] taskReportFieldDescriptors;

    @Before
    public void setUp() {

        taskReportFieldDescriptors = new FieldDescriptor[] {
            fieldWithPath("meta").description("Object holding metainfo on the report"),
            fieldWithPath("meta.name").description("Name of the report"),
            fieldWithPath("meta.date").description("Date of the report creation"),
            fieldWithPath("meta.header").description("Column-headers of the report"),
            fieldWithPath("meta.rowDesc").description("Descriptions for the rows the report"),
            fieldWithPath("meta.totalDesc").description("Description for the sum column"),
            fieldWithPath("rows").description("Array holding the rows of the report."),
            fieldWithPath("rows[].cells").description("Array holding all the cell values of the given row"),
            fieldWithPath("rows[].total").description("Sum of all values of the given row"),
            fieldWithPath("rows[].depth").description(
                "Depth of the row. If the depth is > 0, then this row is a sub-row of a prior row"),
            fieldWithPath("rows[].desc").description("Array containing description of the row."),
            fieldWithPath("rows[].display").description(
                "Boolean identifying if the given row should be initially displayed or not."),
            subsectionWithPath("sumRow").description(
                "Array holding the sums in the columns over all rows. Structure same as 'rows'"),
            fieldWithPath("_links.self.href").ignored()
        };
    }

    @Test
    public void getTaskStatusReport() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/monitor/tasks-status-report")
            .header("Authorization", "Basic YWRtaW46YWRtaW4="))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetTaskStatusReportDocTest",
                responseFields(taskReportFieldDescriptors)));
    }

    @Test
    public void tasksWorkbasketReport() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port
                + "/api/v1/monitor/tasks-workbasket-report?daysInPast=4&states=READY,CLAIMED,COMPLETED")
            .accept("application/hal+json")
            .header("Authorization", "Basic YWRtaW46YWRtaW4="))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetTaskWorkbasketReportDocTest",
                responseFields(taskReportFieldDescriptors)));
    }

    @Test
    public void tasksClassificationReport() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/monitor/tasks-classification-report")
            .accept("application/hal+json")
            .header("Authorization", "Basic YWRtaW46YWRtaW4="))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetTaskClassificationReportDocTest",
                responseFields(taskReportFieldDescriptors)));
    }

    @Test
    public void getTimestampReport() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/monitor/timestamp-report")
            .accept("application/hal+json")
            .header("Authorization", "Basic YWRtaW46YWRtaW4="))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetTimestampReportDocTest",
                responseFields(taskReportFieldDescriptors)));
    }
}
