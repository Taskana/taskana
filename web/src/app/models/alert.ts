export enum AlertType {
	SUCCESS = 'success',
	INFO = 'info',
	WARNING = 'warning',
	DANGER = 'danger',
}

export class AlertModel {

	constructor(public type: string = AlertType.SUCCESS,
		public text: string = 'Success',
		public autoClosing: boolean = true,
		public closingDelay: number = 2500) {
	}
}
