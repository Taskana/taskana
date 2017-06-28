import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { Category } from '../categoriesadministration/category';
import { CategoryService } from '../services/category.service';

@Component({
  selector: 'app-categoriestree',
  templateUrl: './categoriestree.component.html',
  styleUrls: ['./categoriestree.component.css'],
  providers: [CategoryService]
})
export class CategoriestreeComponent implements OnInit {

  categories: Category[];
  errorMessage: string;

  nodes = [];

/*
  nodes = [
    {
      id: '1',
      name: 'Category 1',
      children: [
        { id: '2', name: 'Category 2' },
        { id: '3', name: 'Category 3' }
      ]
    },
    {
      id: '4',
      name: 'Category 4',
      children: [
        { id: '5', name: 'Category 5' },
        {
          id: '6',
          name: 'Category 6',
          children: [
            { id: '7', name: 'Category 7' }
          ]
        }
      ]
    }
  ];
*/
  

  constructor(private categoryService: CategoryService) {
  }

  ngOnInit() {
    this.getCategories();
  }

  @Output()
  categorySelected = new EventEmitter<Category>();

  onCategorySelected(arg) {
    console.log("Selected: ", arg);
    var category: Category = arg.node.data;
    this.categorySelected.next(category);
  }

  getCategories() {
    console.log("Going to load categories...");
    this.categoryService.getCategories()
      .subscribe(c => {
        this.categories = c;
        console.log("RESPONSE: ", c);
        this.nodes = c;
        if (this.categories != null && this.categories.length > 0) {
 //         this.nodes = [];
          this.categories.forEach(category => {
            console.log("Geladene Category: ", category);
//            let count = 1;
//            this.nodes.push({'1', 'NAME'});
//            count++;
          });
        }
      });

    
      
    //  this.autoCompleteData.push(workbasket.name);
    //      categories => this.categories = heroes,
    //      error => this.errorMessage = <any>error);
  }

}
