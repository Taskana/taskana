import { Component, OnInit, Input } from '@angular/core';
import { Subscription } from 'rxjs';

import { Workbasket } from '../../../model/workbasket';
import { WorkbasketAuthorization } from '../../../model/workbasket-authorization';

import { WorkbasketService } from '../../../services/workbasket.service';

@Component({
	selector: 'taskana-workbasket-authorizations',
	templateUrl: './authorizations.component.html',
	styleUrls: ['./authorizations.component.scss']
})
export class AuthorizationsComponent implements OnInit {

	@Input()
	workbasket: Workbasket;

	authorizations: Array<WorkbasketAuthorization>;
	authorizationsClone: Array<WorkbasketAuthorization>;
	authorizationsResetClone: Array<WorkbasketAuthorization>;
	newAuthorization: WorkbasketAuthorization = new WorkbasketAuthorization();
	authorizationState: Array<boolean>;
	requestInProgress: boolean = false;
	modalSpinner: boolean = true;
	modalTitle: string;
	modalErrorMessage: string;
	authorizationSubscription: Subscription;

	constructor(private workbasketService: WorkbasketService) { }

	ngOnInit() {
		this.authorizationSubscription = this.workbasketService.getWorkBasketAuthorizations(this.workbasket.workbasketId).subscribe( (authorizations: Array<WorkbasketAuthorization>) =>{
			this.authorizations = authorizations;
			this.authorizationsClone = this.cloneAuthorizations(this.authorizations);
			this.authorizationsResetClone = this.cloneAuthorizations(this.authorizations);
		})
	}

	addAuthorization(){
		this.authorizations.push({...this.newAuthorization});
		this.authorizationsClone.push({...this.newAuthorization});
		this.authorizationState.push(false);
		this.newAuthorization = new WorkbasketAuthorization();
	}

	authorizationToggle(index: number) {
		this.checkAuthorizationState(index);
	}

	clear() {
		this.authorizations = this.cloneAuthorizations(this.authorizationsResetClone);
		this.authorizationsClone = this.cloneAuthorizations(this.authorizationsResetClone);
	}

	remove(index: number) {
		this.authorizations.splice(index,1);
		this.authorizationsClone.splice(index,1);
	}

	save(index: number) {
		
	}

	private resetAuthorizationsState() {
		this.authorizationState = new Array<boolean>(this.authorizations.length);
	}

	private checkAuthorizationState(index: number)	{
		this.authorizationState[index] = false;
		Object.keys(this.authorizations[index]).forEach(key =>{
			if(this.authorizations[index][key]!== this.authorizationsClone[index][key]){
				this.authorizationState[index] = true;
				return;
			}
		});
	}

	private cloneAuthorizations(inputAuthorization): Array<WorkbasketAuthorization>{
		let authorizationClone = new Array<WorkbasketAuthorization>();
		inputAuthorization.forEach(authorization => {
			authorizationClone.push({... authorization});
		});
		this.resetAuthorizationsState();
		return authorizationClone;
	}

	private ngOnDestroy(): void {
		if (this.authorizationSubscription) { this.authorizationSubscription.unsubscribe(); }
	}
}
