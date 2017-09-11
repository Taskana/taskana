import { Component, OnInit, Input } from '@angular/core';
import { Category } from '../categoriesadministration/category';

@Component({
  selector: 'app-categoryeditor',
  templateUrl: './categoryeditor.component.html',
  styleUrls: ['./categoryeditor.component.css']
})
export class CategoryeditorComponent implements OnInit {

  constructor() { }

  ngOnInit() {
    this.categorySelected = new Category('', '', '', '', 0,'');
  }

  @Input()
  categorySelected: Category;

}
