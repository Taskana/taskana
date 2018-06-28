import { Component, Input, OnInit } from '@angular/core';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition/workbasket-definition.service';
import { DomainService } from 'app/services/domain/domain.service';
import { ImportType } from 'app/models/import-type';
import { ErrorModel } from 'app/models/modal-error';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';

@Component({
  selector: 'taskana-import-export-component',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {

  @Input() currentSelection: ImportType;
  domains: string[] = [];

  constructor(private domainService: DomainService, private workbasketDefinitionService: WorkbasketDefinitionService,
    private classificationDefinitionService: ClassificationDefinitionService, private errorModalService: ErrorModalService) {
  }

  ngOnInit() {
  }

  updateDomains() {
    this.domainService.getDomains().subscribe(
      data => this.domains = data
    );
  }

  onSelectFile(event) {
    const file = event.target.files[0];

    const ending = file.name.match(/\.([^\.]+)$/)[1];
    switch (ending) {
        case 'json':
            break;
        default:
            file.value = '';
            this.errorModalService.triggerError(new ErrorModel(undefined,
              `This file format is not allowed! Please use a .json file.`));
    }

    const reader = new FileReader();
    if (this.currentSelection === ImportType.WORKBASKETS) {
      reader.onload = <Event>(e) => {
        this.workbasketDefinitionService.importWorkbasketDefinitions(e.target.result);
      }
    } else {
      reader.onload = <Event>(e) => this.classificationDefinitionService.importClassifications(e.target.result);
    }
    reader.readAsText(file);
  }

  export(domain = '') {
    if (this.currentSelection === ImportType.WORKBASKETS) {
      this.workbasketDefinitionService.exportWorkbaskets(domain);
    } else {
      this.classificationDefinitionService.exportClassifications(domain);
    }
  }
}
