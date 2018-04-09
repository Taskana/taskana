import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { ClassificationDefinition } from 'app/models/classification-definition';
import { ACTION } from 'app/models/action';

import { ClassificationsService } from 'app/services/classifications/classifications.service';
import { MasterAndDetailService } from 'app/services/masterAndDetail/master-and-detail.service';

@Component({
  selector: 'taskana-classification-details',
  templateUrl: './classification-details.component.html',
  styleUrls: ['./classification-details.component.scss']
})
export class ClassificationDetailsComponent implements OnInit, OnDestroy {

  classification: ClassificationDefinition;
  selectedId: string = undefined;
  showDetail = false;
  requestInProgress = false;
  classificationTypes: Array<string> = [];
  badgeMessage = '';
  private action: any;
  private classificationServiceSubscription: Subscription;
  private classificationSelectedSubscription: Subscription;
  private routeSubscription: Subscription;
  private masterAndDetailSubscription: Subscription;


  constructor(private classificationsService: ClassificationsService,
    private route: ActivatedRoute,
    private router: Router,
    private masterAndDetailService: MasterAndDetailService) { }


  ngOnInit() {

    this.classificationsService.getClassificationTypes().subscribe((classificationTypes: Array<string>) => {
      this.classificationTypes = classificationTypes;
    })
    this.classificationSelectedSubscription = this.classificationsService.getSelectedClassification()
      .subscribe(classificationIdSelected => {
        this.classification = undefined;
        if (classificationIdSelected) {
          this.getClassificationInformation(classificationIdSelected);
        }
      });

    this.routeSubscription = this.route.params.subscribe(params => {
      let id = params['id'];
      this.action = undefined;
      if (id && id.indexOf('new-classification') !== -1) {
        this.action = ACTION.CREATE;
        id = undefined;
        this.badgeMessage = 'Creating new workbasket';
        this.getClassificationInformation(id);
      }

      if (id && id !== '') {
        this.selectClassification(id);
      }
    });

    this.masterAndDetailSubscription = this.masterAndDetailService.getShowDetail().subscribe(showDetail => {
      this.showDetail = showDetail;
    });
  }


  backClicked(): void {
    this.classificationsService.selectClassification(undefined);
    this.router.navigate(['./'], { relativeTo: this.route.parent });
  }

  selectType(type: string) {
    this.classification.type = type;
  }
  removeClassification() { }

  onSave() { }

  onClear() { }


  private selectClassification(id: string) {
    this.selectedId = id;
    this.classificationsService.selectClassification(id);
  }
  private getClassificationInformation(classificationIdSelected: string) {
    if (this.action === ACTION.CREATE) { // CREATE
      this.classification = new ClassificationDefinition();
    } else {
      this.requestInProgress = true;
      this.classificationServiceSubscription = this.classificationsService.getClassification(classificationIdSelected)
        .subscribe((classification: ClassificationDefinition) => {
          this.classification = classification;
          this.requestInProgress = false;
        });
    }
  }

  ngOnDestroy(): void {
    if (this.masterAndDetailSubscription) { this.masterAndDetailSubscription.unsubscribe(); }
    if (this.routeSubscription) { this.routeSubscription.unsubscribe(); }
    if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }
    if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }

  }
}
