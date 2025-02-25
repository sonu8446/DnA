export interface IPhase {
  id: string;
  name: string;
}

export interface ILocation {
  id: string;
  name: string;
  is_row: boolean;
}

export interface IDigitalizationMilestone {
  id?: string;
  sgk: string; // overhead
  mib: string;
  fte: string; // kapa
  date: string;
  phase: IPhase | null;
}

export interface IDigitalizationRollout {
  id?: string;
  hangar: IHangar | null;
  plant: IPlant | null;
  model: string;
  sgk: string; // overhead
  mib: string;
  fte: string; // kapa
  endDate: string;
}

export interface IMBCMilestone {
  id?: string;
  sgk: string; // overhead
  mib: string;
  fte: string; // kapa
  date: string;
  phase: IPhase | null;
}

export interface IMBCRollout {
  id?: string;
  hangar: IHangar | null;
  plant: IPlant | null;
  model: string;
  sgk: string; // overhead
  mib: string;
  fte: string; // kapa
  endDate: string;
  location: ILocation | null;
}

export interface IMilestone {
  id?: string;
  name: string;
  date: string;
}
export interface INotebookInfo {
  id?: string;
  solutionId?: string;
  name: string;
  description: string;
  createdOn?: string;
}
export interface INotebookInfoSolutionId {
  name: string;
  description: string;
  solutionId: string;
}
export interface INotebookInfoData {
  data: INotebookInfo;
}
export interface ISubsription {
  data: {
    appName: string;
    description: string;
  };
}
export interface IUsecase {
  id: string;
  createdDate: string;
  lastmodifieddate?: string;
  title: string;
  isPublished: boolean;
  description: string;
  tags: ITag[];
  projectStatus: IProjectStatus | null;
  projectStatusComment: string;
  createdBy: IUserInfo | null;
  responsiblePL: IUserInfo | null;
  sponsor: IUserInfo | null;
  plant: IPlant | null;
  craft: ICraft | null;
  currentPhase: IPhase | null;
  followedByCurrentUser: boolean;
}

export interface IProjectStatus {
  id: string;
  name: string;
}
export interface IChips {
  id: string;
  name: string;
}
export interface IProjectType {
  id: string;
  name: string;
  routePath: string;
}

export interface ITag {
  id: string;
  name: string;
}

export interface IDigiUsecase extends IUsecase {
  ambition: string;
  responsibleITContact: IUserInfo | null;
  milestones: IDigitalizationMilestone[];
  rollouts: IDigitalizationRollout[];
  startingSituation: string;
  digitalTransformation: string;
  productPortfolio: string;
  quality: string;
  productPlacement: string;
  flexibility: string;
  productivity: string;
  sgkYearly: string;
  eakYearly: string;
  mibYearly: string;
  fteYearly: string;
  sgkOnce: string;
  eakOnce: string;
  mibOnce: string;
  fteOnce: string;
  topic: ITopic | null;
  thumbnailId: string;
  hasProcess: boolean;
  hasParts: boolean;
  hasProduct: boolean;
  hasPeople: boolean;
  hasProductPortfolio: boolean;
  hasFlexibility: boolean;
  hasProductivity: boolean;
  hasQuality: boolean;
  hasProductPlacement: boolean;
  hasDigitalTransformation: boolean;
  usecaseImagesIds: string[];
  businessId: string;
  pilot: string;
}

export interface IMBCUsecase extends IUsecase {
  ambition: string;
  responsibleITContact: IUserInfo | null;
  milestones: IDigitalizationMilestone[];
  rollouts: IMBCRollout[];
  startingSituation: string;
  digitalTransformation: string;
  productPortfolio: string;
  quality: string;
  productPlacement: string;
  flexibility: string;
  productivity: string;
  sgkYearly: string;
  eakYearly: string;
  mibYearly: string;
  fteYearly: string;
  sgkOnce: string;
  eakOnce: string;
  mibOnce: string;
  fteOnce: string;
  topic: ITopic | null;
  thumbnailId: string;
  hasProcess: boolean;
  hasParts: boolean;
  hasProduct: boolean;
  hasPeople: boolean;
  hasProductPortfolio: boolean;
  hasFlexibility: boolean;
  hasProductivity: boolean;
  hasQuality: boolean;
  hasProductPlacement: boolean;
  hasDigitalTransformation: boolean;
  usecaseImagesIds: string[];
  businessId: string;
  pilot: string;
  productOwners: IUserInfo[];
  dataSolutionSponsors: IUserInfo[];
  dataScientists: IUserInfo[];
  dataEngineers: IUserInfo[];
  itSupports: IUserInfo[];
  division: IDivision | null;
  subDivision: ISubDivision | null;
  isDigiUseCase: boolean;
  locations: ILocation[];
  totalDataVolume: IDataVolume | null;
  isSolutionOnCloud: boolean;
  gitUrl: string;
  kpis: IKpi[];
  budgetValue: number | null;
  budgetValueComment: string;
  budgetEffort: number | null;
  budgetEffortComment: string;
  dataSources: IDataSource[];
  usesExistingInternalPlatforms: boolean;
  platforms: IPlatform[];
  languages: ILanguage[];
  algorithms: IAlgorithm[];
  visualizations: IVisualization[];
  result: IResult | null;
  resultUrl: string;
  budgetLegitimates: IUserInfo[];
}

export interface ICraftSubCategory {
  id: string;
  name: string;
}

export interface ITopic {
  id: string;
  name: string;
}

export interface IProjectClassification {
  id: string;
  name: string;
}

export interface IProjectOutputEffect {
  id: string;
  name: string;
}

export interface ISubProject {
  id: string;
  name: string;
}

export interface IMainProject {
  id: string;
  name: string;
}

export interface IProjectsUseCase extends IUsecase {
  isPrivate: boolean;
  wePhrase: IWePhrases | null;
  moColumn: IMoColumns | null;
  deputyPL: IUserInfo | null;
  projectClassification: IProjectClassification | null;
  hasBrSiteRelevance: boolean;
  hasBrCenterRelevance: boolean;
  hasGlSiteRelevance: boolean;
  hasGlProductionRelevance: boolean;
  budget: number;
  affectedCenters: IDepartment[];
  affectedDepartments: IDepartment[];
  projectTeam: IUserInfo[];
  isMainProject: boolean;
  milestones: IMilestone[];
  mainProjectId: string | null;
  subProjects: ISubProject[] | null;
  projectOutputEffects: IProjectOutputEffect[];
  dependentMilestoneProjectIds: string[];
  dependentMilestones: IDependentMilestone[];
}

export interface IDependentMilestone {
  targetMilestoneId: string; // depends on from another project
  sourceMilestoneId: string; // in usecase
}

export interface IWePhrases {
  id: string;
  name: string;
}

export interface IMoColumns {
  id: string;
  name: string;
  color: string;
}

export interface IUserInfo {
  id: string;
  firstName: string;
  lastName: string;
  eMail: string;
  mobileNumber: string;
  department: string;
  favoriteUsecases?: IUserFavoriteUseCase[];
  roles?: IRole[];
}
export interface IUserRequestVO {
  data: IUserInfo;
}
export interface IUserInfoExtended extends IUserInfo {
  digiRole?: IRole;
}
export interface IUserInfoResponse {
  records: IUserInfo[];
  totalCount: number;
}

export interface IPlant {
  id: string;
  name: string;
}

export interface IHangar {
  id: string;
  name: string;
  plant: IPlant | null;
}

export interface ICraft {
  id: string;
  name: string;
  pacemaker: IUserInfo | null;
}

export interface ILocation {
  id: string;
  name: string;
  is_row: boolean;
}

export interface IDepartment {
  id: string;
  name: string;
}
export interface IBusinessGoal {
  id: string;
  name: string;
}

export interface IMaturityLevel {
  id: string;
  name: string;
}

export interface IBenefitRelevance {
  id: string;
  name: string;
}

export interface IStrategicRelevance {
  id: string;
  name: string;
}

export interface IAppVersion {
  currentAppVersion: string;
  minimumAppVersion: string;
}

export interface IProjectUseCaseIdAndTitleAndMilestones {
  id: string;
  title: string;
  milestones: IMilestone[];
}

export interface IUserFavoriteUseCase {
  id: string;
  usecaseId: string;
  userInfoId: string;
}

export interface IRole {
  id: string;
  name: string;
}

export interface IDivision {
  id: string;
  name: string;
}

export interface IDivisionFilterPreference {
  id: string;
  name: string;
  subdivisions: ISubDivisionSolution[];
}

export interface ISubDivision {
  id: string;
  name: string;
}

export interface ISubDivisionSolution {
  id: string;
  name: string;
  division: string;
}

export interface IKpi {
  id: string;
  description: string;
  value: number | undefined;
}

export interface IDataSource {
  dataSources?: string[];
  dataVolume?: IDataVolume;
}

export interface IDataSourceMaster {
  id: number;
  name?: string;
}

export interface IPlatform {
  id: number;
  name?: string;
}

export interface ILanguage {
  id: number;
  name?: string;
}

export interface IAlgorithm {
  id: number;
  name?: string;
}

export interface IVisualization {
  id: number;
  name?: string;
}

export interface IResult {
  id: string;
  name?: string;
}

export interface IDataVolume {
  id?: string;
  name?: string;
}

export interface IDivisionAndSubDivision {
  id: string;
  name: string;
  subdivision: ISubDivision;
}

export interface IMilestones {
  milestoneDescription: string;
  month: number;
  phase: IPhase;
  year: number;
}

export interface IPhasesItem {
  description: string;
  month: number;
  phase: IPhase;
  year: number;
}

export interface IRolloutDetail {
  location: ILocation;
  month: number;
  year: number;
}

export interface IRollout {
  details: IRolloutDetail[];
  description: string;
}

export interface IMilestonesList {
  phases: IPhasesItem[];
  rollouts: IRollout;
}

export interface ITeams {
  company?: string;
  department?: string;
  email?: string;
  firstName?: string;
  shortId?: string;
  lastName?: string;
  mobileNumber?: string;
  teamMemberPosition?: string;
  userType: string;
}
export interface IUserDetails {
  firstName: string;
  shortId: string;
  department: string;
  email: string;
  id?: string;
  lastName: string;
  mobileNumber?: string;
}
export interface IError {
  message: string;
}

export interface ICreateNewSolution {
  businessGoal: string;
  businessNeed: string;
  createdDate?: string;
  dataSources?: IDataSource;
  description: string;
  reasonForHoldOrClose?: string;
  division: IDivisionAndSubDivision;
  expectedBenefits: string;
  id?: string;
  lastmodifiedDate?: string;
  locations?: ILocation[];
  currentPhase?: IPhase;
  relatedProducts: string[];
  milestones?: IMilestonesList;
  analytics?: IAnalytics;
  sharing?: ISharing;
  productName: string;
  projectStatus: IProjectStatus;
  openSegments: string[];
  tags: string[];
  team?: ITeams[];
  digitalValue?: IDigitalValue;
  portfolio?: IPortfolio;
  publish: boolean;
  createdBy?: IUserInfo;
  dataCompliance?: IDataCompliance;
  logoDetails: ILogoDetails;
  attachments: IAttachment[];
}

export interface IDataiku {
  name: string;
  shortDesc: string;
  projectKey: string;
  tags: string[];
  ownerDisplayName: string;
  role: string;
  projectStatus?: string;
  contributors?: string[];
  projectType?: string;
  checklists?: IDataikuChecklistObject;
  creationTag?: IDataikuCreation;
  solutionId?: string;
  ownerLogin?: string;
  projectAppType?: string;
  tutorialProject?: boolean;
  disableAutomaticTriggers?: boolean;
  commitMode?: string;
  isProjectAdmin?: boolean;
  canReadProjectContent?: boolean;
  canWriteProjectContent: boolean;
  canModerateDashboards?: boolean;
  canWriteDashboards?: boolean;
  canManageDashboardAuthorizations?: boolean;
  canManageExposedElements?: boolean;
  canManageAdditionalDashboardUsers?: boolean;
  canExportDatasetsData?: boolean;
  canReadDashboards?: boolean;
  canRunScenarios?: boolean;
  canExecuteApp?: boolean;
  objectImgHash?: any;
  isProjectImg?: boolean;
  defaultImgColor?: string;
  imgPattern?: number;
  metrics?: any;
  metricsChecks?: any;
  sparkPipelinesEnabled?: boolean;
  sqlPipelinesEnabled?: boolean;
  versionTag?: any;
  customFields?: any;
}

export interface IDataikuCreation {
  versionNumber: number;
  lastModifiedBy: IDataikuModifiedBy;
  lastModifiedOn: any;
}

export interface IDataikuModifiedBy {
  login: string;
}

export interface IDataikuChecklistObject {
  checklists?: IDataikuCheckList[];
}

export interface IDataikuCheckList {
  title: string;
  createdOn: any;
  items: IDataikuCheckListItem[];
}

export interface IDataikuCheckListItem {
  done: boolean;
  text: string;
  createdOn: any;
  createdBy: string;
  stateChangedOn: any;
}
export interface ICreateNewSolutionRequest {
  data: ICreateNewSolution;
}

export interface ICreateNewSolutionResult {
  data?: ICreateNewSolution;
  errors?: IError[];
}
export interface IGetMalwarescanSubscriList {
  data?: ICreateNewSolution;
  errors?: IError[];
}
export interface IGetDataikuResult {
  data?: IDataiku[];
  totalCount?: number;
  errors?: IError[];
}
export interface IMonth {
  id: number;
  name: string;
}
export interface IAllSolutionsListItem {
  id?: string;
  productName: string;
  description: string;
  logoDetails: ILogoDetails;
  division: IDivisionAndSubDivision;
  team?: ITeams[];
  portfolio?: IPortfolio;
  projectStatus: IProjectStatus;
  locations?: ILocation[];
  currentPhase?: IPhase;
  digitalValue?: IDigitalValue;
  publish: boolean;
  bookmarked: boolean;
  createdBy: IUserInfo;
}

export interface IAllSolutionsListItemCSV {
  id?: string;
  businessNeed: string;
  businessGoal: string;
  relatedProducts: string[];
  createdDate?: string;
  dataSources?: IDataSource;
  description: string;
  reasonForHoldOrClose?: string;
  division: IDivisionAndSubDivision;
  expectedBenefits: string;
  lastModifiedDate?: string;
  locations?: ILocation[];
  currentPhase?: IPhase;
  milestones?: IMilestonesList;
  analytics?: IAnalytics;
  sharing?: ISharing;
  productName: string;
  projectStatus: IProjectStatus;
  openSegments: string[];
  tags: string[];
  team?: ITeams[];
  digitalValue?: IDigitalValue;
  portfolio?: IPortfolio;
  publish: boolean;
  createdBy?: IUserInfo;
  dataCompliance?: IDataCompliance;
  attachments: IAttachment[];
  bookmarked: boolean;
}

export interface IAllSolutionsResult {
  records?: IAllSolutionsListItem[];
  totalCount?: number;
}

export interface IAllSolutionsResultCSV {
  records?: IAllSolutionsListItemCSV[];
  totalCount?: number;
}

export interface IPorfolioSolutionItem {
  id?: string;
  productName: string;
  description: string;
  locations?: ILocation[];
  currentPhase?: IPhase;
  dataSources?: IDataSource;
  digitalValue?: IDigitalValue;
  team?: ITeams[];
  portfolio?: IPortfolio;
}
export interface IPortfolioSolutionsData {
  records?: IPorfolioSolutionItem[];
  totalCount?: number;
}
export interface IScatterChartDataItem {
  id: string;
  labelValue: string;
  descriptionValue: string;
  yAxisValue: number;
  xAxisValue: number;
  zAxisValue: number;
}
export interface IBarChartDataItem {
  id: string;
  labelValue: string;
  barValue: number;
  barFillColor: string;
}
export interface ISolutionDigitalValue {
  id: string;
  productName: string;
  description: string;
  digitalValue: number;
}
export interface IBreakEvenYearSolutions {
  breakEvenYear: number;
  solutions: ISolutionDigitalValue[];
}
export interface IStackedBarChartDataItem {
  id: string;
  labelValue: string;
  firstBarValue: number;
  firstSolution?: ISolutionDigitalValue;
  firstBarFillColor: string;
  secondBarValue: number;
  secondBarFillColor: string;
  secondSolution?: ISolutionDigitalValue;
  thirdBarValue: number;
  thirdBarFillColor: string;
  thirdSolution?: ISolutionDigitalValue;
  fourthBarValue: number;
  fourthBarFillColor: string;
  fourthSolution?: ISolutionDigitalValue;
  fifthBarValue: number;
  fifthBarFillColor: string;
  fifthSolution?: ISolutionDigitalValue;
}
export interface ILocationsMapChartDataItem {
  id: string;
  countryName: string;
  coordinates: [number, number];
  countValue: number;
}
export interface IBookMarksResponseData {
  id: string;
}
export interface IBookMarksResponse {
  data?: IBookMarksResponseData;
  errors?: IError[];
}
export interface IBookMarks {
  favoriteUsecases: string[];
  id: string;
  deleteBookmark: boolean;
}
export interface IAccessRoles {
  key: string;
  value: string;
}
export interface IDataEntries {
  id: string;
  name: string;
}
export interface IWidgetsResponse {
  accessRoles: IAccessRoles[];
  dataEntries: IDataEntries[];
  id: string;
  name: string;
  widgetChartType: string;
  widgetDescription: string;
  widgetTitle: string;
  xaxisTitle: string;
  yaxisTitle: string;
}
export interface IWidget {
  accessRoles: IAccessRoles[];
  dataEntries: IDataEntries[];
  id: string;
  name: string;
  widgetChartType: string;
  widgetDescription: string;
  widgetTitle: string;
  xaxisTitle: string;
  yaxisTitle: string;
}
export interface IFilterParams {
  phase: string[];
  division: string[];
  subDivision: string[];
  location: string[];
  status: string[];
  useCaseType: string[];
  dataVolume?: string[];
  tag: string[];
}
export interface IFilterPreferences {
  divisions: IDivision[];
  subDivisions: any[];
  locations: ILocation[];
  phases: IPhase[];
  solutionStatus: IProjectStatus;
  useCaseType?: string;
  tags: ITag[];
}
export interface IUserPreference {
  filterPreferences: IFilterPreferences;
  id?: string;
  userId: string;
  widgetPreferences?: any[];
}
export interface IUserPreferenceRequest {
  data: IUserPreference;
}
export interface IAnalytics {
  languages: ITag[];
  algorithms: ITag[];
  visualizations: ITag[];
}

export interface ISharing {
  gitUrl: string;
  result: IResult;
  resultUrl: string;
}

export interface IAttachment {
  id: string;
  fileName: string;
  fileSize: string;
}

export interface IAttachmentError {
  message: string;
}

export interface ILogoDetails {
  id: string;
  fileName?: string;
  fileSize?: string;
  isPredefined: boolean;
}

export interface ILink {
  link: string;
  label: string;
  description: string;
}

export interface IDataCompliance {
  quickCheck: boolean;
  expertGuidelineNeeded: boolean;
  useCaseDescAndEval: boolean;
  readyForImplementation: boolean;
  attachments: IAttachment[];
  links: ILink[];
  complianceOfficers: ITeams[];
}

export interface IDigitalValue {
  digitalValue?: number;
  digitalValueComment?: string;
  digitalEffort?: number;
  digitalEffortComment?: string;
  costDrivers?: ICostFactor[];
  valueDrivers?: IValueFactor[];
  maturityLevel: string;
  projectControllers: ITeams[];
  attachments: IAttachment[];
  permissions: ITeams[];
  assessment: IAssessment;
  valueCalculator?: IValueCalculator;
}

export interface IValueCalculator {
  calculatedValueRampUpYears: IValueRampUp[];
  costFactorSummary?: ICostValueFactorSummary;
  valueFactorSummary?: ICostValueFactorSummary;
  calculatedDigitalValue?: ICalculatedDigitalValueSummary;
  breakEvenPoint?: string;
}
export interface ICostValueFactorSummary {
  year: string;
  value: string;
}

export interface ICalculatedDigitalValueSummary {
  valueAt: string;
  year: string;
  value: string;
}

export interface IAssessment {
  strategicRelevance: string;
  commentOnStrategicRelevance?: string;
  benefitRealizationRisk: string;
  commentOnBenefitRealizationRisk?: string;
}

export interface ICostFactor {
  description: string;
  category: string;
  value: string;
  source: string;
  rampUp: ICostRampUp[];
}

export interface ICostRampUp {
  year: string;
  value: string;
}

export interface ICostRampUpError {
  year: string;
  value: string;
}

export interface IValueRampUpError {
  year: string;
  percent: string;
  value: string;
}

export interface IValueFactor {
  description: string;
  category: string;
  value: string;
  source: string;
  rampUp: IValueRampUp[];
}

export interface IValueRampUp {
  year: string;
  percent: string;
  value: string;
}

export interface IChangeLogData {
  changeDate: string;
  modifiedBy: ITeams;
  fieldChanged: string;
  oldValue: null | string;
  newValue: string;
  changeDescription: string;
}

export interface IPortfolio {
  dnaSubscriptionAppId: string | null;
  dnaNotebookId: string | null;
  dnaDataikuProjectId: string | null;
  solutionOnCloud: boolean;
  usesExistingInternalPlatforms: boolean;
  platforms: ITag[];
}

export interface IFitlerCategory {
  id: number;
  name: string;
}
export interface ITagResult {
  id: string;
  name: string;
  category: IFitlerCategory;
}
export interface IRelatedProduct {
  id: string;
  name: string;
}

export interface ISubsriptionAdminList {
  id: string;
  apiKey: string;
  appId: string;
  appName: string;
  createdBy: string;
  createdDate: string;
  description: string;
  expiryDays: number | null;
  expireOn: string | null;
  expiryPreference: string;
  recordStatus: string;
  updatedBy: string;
  updatedDate: string;
  usageCount: string;
  solutionId: string;
  lastUsedOn: string;
}

export interface ISubsriptionExpiryObject {
  appId: string;
  description: string;
  expiryDays: number | null;
}

export interface ISubsriptionExpiryObjectData {
  data: ISubsriptionExpiryObject;
}
export interface IPipelineProjectDetail {
  projectId: string;
  projectName: string;
  projectDescription: string;
  isOwner: boolean;
  dags: IPipelineProjectDag[];
}

export interface IPipelineProjectDag {
  forEach: any;
  dagContent: string;
  dagName: string;
  collaborators: IPipelineProjectDagsCollabarator[];
  permissions: string[];
  dagId: number;
  active?: boolean;
}
export interface IPipelineProjectDagData {
  data: IPipelineProjectDag;
}
export interface IPipelineProjectDagsCollabarator {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  permissions: string[];
  id?: number;
}
export interface IModuleUsage {
  component: string;
  version: string;
  license: string;
}
