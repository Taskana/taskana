import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { ImportType } from 'app/models/import-type';
import { Classification } from 'app/models/classification';
import { TreeNode } from 'app/models/tree-node';

import { ClassificationsService } from 'app/services/classifications/classifications.service';

@Component({
	selector: 'taskana-classification-list',
	templateUrl: './classification-list.component.html',
	styleUrls: ['./classification-list.component.scss']
})
export class ClassificationListComponent implements OnInit, OnDestroy {


	selectionToImport = ImportType.CLASSIFICATIONS;
	requestInProgress = false;

	classifications: Array<Classification> = [];
	classificationsTypes: Map<string, string> = new Map();
	classificationTypeSelected: string;
	classificationServiceSubscription: Subscription;
	classificationTypeServiceSubscription: Subscription;
	constructor(private classificationService: ClassificationsService) {
	}

	ngOnInit() {
		this.classificationServiceSubscription = this.classificationService.getClassifications()
			.subscribe((classifications: Array<TreeNode>) => {
				this.classifications = classifications;
				this.classificationTypeServiceSubscription = this.classificationService.getClassificationTypes()
					.subscribe((classificationsTypes: Map<string, string>) => {
						this.classificationsTypes = classificationsTypes;
						this.classificationTypeSelected = this.classifications[0].type;
					});
			});
	}

	selectClassificationType(classificationTypeSelected: string) {
		this.classificationService.getClassifications(true, classificationTypeSelected)
			.subscribe((classifications: Array<TreeNode>) => {
				this.classifications = classifications;
			});
	}

	addClassification() { }
	removeClassification() { }

	ngOnDestroy(): void {
		if (this.classificationServiceSubscription) { this.classificationServiceSubscription.unsubscribe(); }
		if (this.classificationTypeServiceSubscription) { this.classificationTypeServiceSubscription.unsubscribe(); }
	}
}
