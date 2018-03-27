import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkbasketService} from '../../services/workbasket/workbasket.service';
import {ClassificationService} from '../../services/classification/classification.service';
import {WorkbasketDefinitionService} from '../../services/workbasket/workbasketDefinition.service';

@Component({
  selector: 'taskana-import-export-component',
  templateUrl: './import-export.component.html',
  styleUrls: ['./import-export.component.scss']
})
export class ImportExportComponent implements OnInit {

  @Input() currentSelection: string;
  workbasketDomains: string[];
  classificationDomains: string[];

  constructor(private workbasketService: WorkbasketService, private workbasketDefinitionService: WorkbasketDefinitionService,
              private classificationService: ClassificationService) {
  }

  ngOnInit() {
  }

  updateDomains() {
    this.workbasketService.getWorkbasketDomains().subscribe(
      data => this.workbasketDomains = data
    );
    this.classificationService.getClassificationDomains().subscribe(
      data => this.classificationDomains = data
    );
  }

  onSelectFile(event) {
    const file = event.srcElement.files[0];
    const reader = new FileReader();
    if (this.currentSelection === 'workbaskets') {
      reader.onload = <Event>(e) => this.workbasketDefinitionService.importWorkbasketDefinitions(e.target.result);
    } else {
      reader.onload = <Event>(e) => this.classificationService.importClassifications(e.target.result);
    }
    reader.readAsText(file);
  }

  exportAll() {
    if (this.currentSelection === 'workbaskets') {
      this.workbasketDefinitionService.exportAllWorkbaskets();
    } else {
      this.classificationService.exportAllClassifications();
    }
  }

  exportByDomain(domain: string) {
    if (this.currentSelection === 'workbaskets') {
      this.workbasketDefinitionService.exportWorkbasketsByDomain(domain);
    } else {
      this.classificationService.exportClassificationsByDomain(domain);
    }
  }
}
