export class FilterModel {
	type: string;
	name: string;
	description: string;
	owner: string;
	key: string;
	constructor(type: string = '', name: string = '', description: string = '', owner: string = '', key: string = '') {
		this.type = type;
		this.name = name;
		this.description = description;
		this.owner = owner;
		this.key = key;
	}
}
