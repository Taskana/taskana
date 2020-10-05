import { TestBed, async } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Customisation, CustomisationContent } from 'app/shared/models/customisation';
import { asteriskIcon, ClassificationCategoriesService, missingIcon } from './classification-categories.service';

describe('ClassificationCategoriesService', () => {
  let categoryService: ClassificationCategoriesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClassificationCategoriesService]
    });

    categoryService = TestBed.get(ClassificationCategoriesService);
    httpMock = TestBed.get(HttpTestingController);
  });

  it('should insert missing icon into customisation', async(() => {
    const expectedCustomisationContent: CustomisationContent = {
      classifications: { categories: { all: asteriskIcon, missing: missingIcon } }
    };

    const expectedCustomisation: Customisation = { EN: expectedCustomisationContent, DE: expectedCustomisationContent };

    const initialCustomisations: Customisation[] = [
      { EN: { classifications: { categories: {} } }, DE: { classifications: { categories: {} } } },
      { EN: { classifications: {} }, DE: { classifications: {} } },
      { EN: {}, DE: {} }
    ];

    initialCustomisations.forEach((initialCustomisation) => {
      categoryService.getCustomisation().subscribe((customisation) => {
        expect(customisation).toEqual(expectedCustomisation);
      });

      httpMock.expectOne('environments/data-sources/taskana-customization.json').flush(initialCustomisation);

      httpMock.verify();
    });
  }));
});
