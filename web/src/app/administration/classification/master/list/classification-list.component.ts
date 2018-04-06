import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { Router, ActivatedRoute } from '@angular/router';

import { ImportType } from 'app/models/import-type';
import { Classification } from 'app/models/classification';
import { TreeNodeModel } from 'app/models/tree-node';

import { ClassificationsService } from 'app/services/classifications/classifications.service';

@Component({
	selector: 'taskana-classification-list',
	templateUrl: './classification-list.component.html',
	styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit, OnDestroy {


	selectedId: string;
	selectionToImport = ImportType.CLASSIFICATIONS;
	requestInProgress = false;

	classifications: Array<Classification> = [];
	classificationsTypes: Array<string> = [];
	classificationTypeSelected: string;
	classificationServiceSubscription: Subscription;
	classificationTypeServiceSubscription: Subscription;
	classificationSelectedSubscription: Subscription;

	constructor(
		private classificationService: ClassificationsService,
		private router: Router,
		private route: ActivatedRoute, ) {
	}

	ngOnInit() {
		this.classificationServiceSubscription = this.classificationService.getClassifications()
			.subscribe((classifications: Array<TreeNodeModel>) => {
				this.classifications = classifications;
				this.classificationTypeServiceSubscription = this.classificationService.getClassificationTypes()
					.subscribe((classificationsTypes: Array<string>) => {
						this.classificationsTypes = classificationsTypes;
						this.classificationTypeSelected = this.classifications[0].type;
					});
			});
		this.classificationSelectedSubscription = this.classificationService.getSelectedClassification()
			.subscribe((classificationSelected: string) => {
				// TODO should be done in a different way.
				setTimeout(() => { this.selectedId = classificationSelected; }, 0);

			});
	}

	selectClassificationType(classificationTypeSelected: string) {
		this.classificationService.getClassifications(true, classificationTypeSelected)
			.subscribe((classifications: Array<TreeNodeModel>) => {
				this.classifications = classifications;
			});
	}
	selectClassification(id: string) {
		this.selectedId = id;
		this.router.navigate([{ outlets: { detail: [this.selectedId] } }], { relativeTo: this.route });
	}

	addClassification() {
		this.selectedId = undefined;
		this.router.navigate([{ outlets: { detail: ['new-classification'] } }], { relativeTo: this.route });
	}

	ngOnDestroy(): void {
		if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
		if (this.classificationTypeServiceSubscription) { this.classificationTypeServiceSubscription.unsubscribe(); }
		if (this.classificationSelectedSubscription) { this.classificationSelectedSubscription.unsubscribe(); }

	}
}
