This Module is a clone of Projekt Module 
[camunda-dmn-xlsx:xlsx-dmn-converter](https://github.com/camunda-community-hub/camunda-dmn-xlsx/tree/master/xlsx-dmn-converter)

The original project is licensed under APACHE LICENSE Version 2.

All changes to the code are included in this PR: https://github.com/camunda-community-hub/camunda-dmn-xlsx/pull/53

The changes were necessary so that there is a version compatible with jakarta-xml-bind and this version can be used with Camunda.

The main changes were:

* Upgrade of lib `org.docx4j:docx4j:6.0.1` to `org.docx4j:docx4j-JAXB-ReferenceImpl:11.4.9`
* In class `org.camunda.bpm.dmn.xlsx.XlsxConverter` and `org.camunda.bpm.dmn.xlsx.XlsxWorksheetConverter` a new parameter was introduced: `historyTimeToLive`
  * because camunda-engine with version 7.20.X requires a timeToLive for history of decisions
  * default timeToLive is 180 days
  * the new parameter is set during execution of convert method to the created decisons
