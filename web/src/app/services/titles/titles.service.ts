import { Injectable } from '@angular/core';

@Injectable()
export class TitlesService {

  titles = new Map<number, string>();
  customizedTitles: any = {};
  constructor() { }

  initTitles(language: string = 'EN', jsonFile: any) {
    this.titles = jsonFile[language];
  }

  getTitle(id: number, fallBacktext: string, customPath: string = undefined) {

    return this.titles[id] ? this.titles[id] : fallBacktext
  }
}
