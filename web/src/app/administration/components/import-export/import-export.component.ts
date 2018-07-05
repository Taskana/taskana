import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { ClassificationDefinitionService } from 'app/administration/services/classification-definition/classification-definition.service';
import { WorkbasketDefinitionService } from 'app/administration/services/workbasket-definition/workbasket-definition.service';
import { DomainService } from 'app/services/domain/domain.service';
import { TaskanaType } from 'app/models/taskana-type';
import { ErrorModel } from 'app/models/modal-error';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';

@Component({
  selector: 'taskana-import-export-component',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {

  @Input() currentSelection: TaskanaType;

  @Output() importSucessful = new EventEmitter();


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
    if (this.currentSelection === TaskanaType.WORKBASKETS) {
      reader.onload = <Event>(e) => this.workbasketDefinitionService.importWorkbasketDefinitions(e.target.result);
      this.importSucessful.emit();
    } else {
      reader.onload = <Event>(e) => this.classificationDefinitionService.importClassifications(e.target.result);
      this.importSucessful.emit();
    }
    reader.readAsText(file);
  }

  export(domain = '') {
    if (this.currentSelection === TaskanaType.WORKBASKETS) {
      this.workbasketDefinitionService.exportWorkbaskets(domain);
    } else {
      this.classificationDefinitionService.exportClassifications(domain);
    }
  }
}
