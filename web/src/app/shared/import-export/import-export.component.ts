import { Component, Input, OnInit } from '@angular/core';
import { ClassificationService } from 'app/services/classification/classification.service';
import { WorkbasketDefinitionService } from 'app/services/workbasket/workbasketDefinition.service';
import { DomainService } from 'app/services/domains/domain.service';
import { ImportType } from 'app/models/import-type';

@Component({
  selector: 'taskana-import-export-component',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {

  @Input() currentSelection: ImportType;
  domains: string[] = [];

  constructor(private domainService: DomainService, private workbasketDefinitionService: WorkbasketDefinitionService,
    private classificationService: ClassificationService) {
  }

  ngOnInit() {
  }

  updateDomains() {
    this.domainService.getDomains().subscribe(
      data => this.domains = data
    );
  }

  onSelectFile(event) {
    const file = event.srcElement.files[0];
    const reader = new FileReader();
    if (this.currentSelection === ImportType.WORKBASKETS) {
      reader.onload = <Event>(e) => this.workbasketDefinitionService.importWorkbasketDefinitions(e.target.result);
    } else {
      reader.onload = <Event>(e) => this.classificationService.importClassifications(e.target.result);
    }
    reader.readAsText(file);
  }

  export(domain = '') {
    if (this.currentSelection === ImportType.WORKBASKETS) {
      this.workbasketDefinitionService.exportWorkbaskets(domain);
    } else {
      this.classificationService.exportClassifications(domain);
    }
  }
}
