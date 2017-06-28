import { WorkplacePage } from './app.po';

describe('workplace App', () => {
  let page: WorkplacePage;

  beforeEach(() => {
    page = new WorkplacePage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
