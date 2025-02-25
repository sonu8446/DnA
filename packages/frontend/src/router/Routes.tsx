import * as React from 'react';
import { Route, Router, Switch } from 'react-router-dom';
import Progress from '../components/progress/Progress';
import { ProtectedRoute } from './../decorators/ProtectedRoute';
import { USER_ROLE } from './../globals/constants';
import { history } from './History';
import { NotFoundPage } from './NotFoundPage';
import { SessionExpired } from './SessionExpired';
import { UnAuthorised } from './UnAuthorised';
import { getTranslatedLabel } from '../globals/i18n/TranslationsProvider';

const Administration = React.lazy(() => import('../components/mbc/admin/Administration'));
const AuthRedirector = React.lazy(() => import('./AuthRedirector'));
const AllSolutions = React.lazy(() => import('../components/mbc/allSolutions/AllSolutions'));
const CreateNewSolution = React.lazy(() => import('../components/mbc/createNewSolution/CreateNewSolution'));
const DssProjectsList = React.lazy(() => import('../components/mbc/dataiku/ListProjects'));
const License = React.lazy(() => import('../components/mbc/footer/License/License'));
const Home = React.lazy(() => import('../components/mbc/home/Home'));
const Notebook = React.lazy(() => import('../components/mbc/notebook/Notebook'));
const Portfolio = React.lazy(() => import('../components/mbc/Portfolio'));
const SearchResults = React.lazy(() => import('../components/mbc/searchResults/SearchResults'));
const Summary = React.lazy(() => import('../components/mbc/summary/Summary'));
const MalwareScanService = React.lazy(() => import('../components/mbc/malwareScanService/MalwareScanService'));
// const Notifications = React.lazy(() => import('../components/mbc/notification/Notifications'));
const Pipeline = React.lazy(() => import('../components/mbc/pipeline/Pipeline'));
const Workspaces = React.lazy(() => import('../components/mbc/workspaces/Workspaces'));
const Services = React.lazy(() => import('../components/mbc/services/Services'));
const CreateNewPipeline = React.lazy(() => import('../components/mbc/pipeline/createNewPipeline/CreateNewPipeline'));
const EditCode = React.lazy(() => import('../components/mbc/pipeline/editCode/EditCode'));
const Comingsoon = React.lazy(() => import('../components/mbc/comingsoon/Comingsoon'));
const UserAndAdminRole = [USER_ROLE.USER, USER_ROLE.EXTENDED, USER_ROLE.ADMIN];
const AdminRole = [USER_ROLE.ADMIN];

const publicRoutes = [
  {
    component: AuthRedirector,
    exact: true,
    path: '/',
  },
  {
    component: SessionExpired,
    exact: true,
    path: '/SessionExpired',
  },
  {
    component: UnAuthorised,
    exact: true,
    path: '/UnAuthorised',
  },
];

const protectedRoutes = [
  {
    allowedRoles: UserAndAdminRole,
    component: Portfolio,
    exact: false,
    path: '/portfolio',
    title: 'Portfolio',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: Home,
    exact: false,
    path: '/home',
    title: 'Home',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: Notebook,
    exact: false,
    path: '/notebook',
    title: 'My Workspace',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: License,
    exact: false,
    path: '/license',
    title: 'License',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: Summary,
    exact: false,
    path: '/summary/:id',
    title: 'Solution Summary',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: MalwareScanService,
    exact: false,
    path: '/malwarescanservice',
    title: 'Malware Scan Service',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: CreateNewSolution,
    exact: false,
    path: '/createnewsolution',
    title: 'Create New Solution',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: CreateNewSolution,
    exact: false,
    path: '/editSolution/:id?/:editable?',
    title: 'Edit Solution',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: AllSolutions,
    exact: false,
    path: '/allsolutions',
    title: 'All Solutions',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: AllSolutions,
    exact: false,
    path: '/viewsolutions/:kpi/:value?',
    title: 'View Solutions',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: AllSolutions,
    exact: false,
    path: '/bookmarks',
    title: 'My Bookmarks',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: AllSolutions,
    exact: false,
    path: '/mysolutions',
    title: 'My Solutions',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: SearchResults,
    exact: false,
    path: '/search/:query',
    title: 'Search',
  },
  {
    allowedRoles: AdminRole,
    component: Administration,
    exact: false,
    path: '/administration',
    title: 'Administration',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: DssProjectsList,
    exact: false,
    path: '/mydataiku',
    title: 'My Dataiku',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: Pipeline,
    exact: false,
    path: '/pipeline',
    title: 'Pipeline',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: Workspaces,
    exact: false,
    path: '/workspaces',
    title: 'Workspaces',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: Services,
    exact: false,
    path: '/services',
    title: 'Services',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: CreateNewPipeline,
    exact: false,
    path: '/createnewpipeline',
    title: 'New Pipeline Project',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: CreateNewPipeline,
    exact: false,
    path: '/createnewpipeline/:id?/:editable?',
    title: 'Edit Pipeline Project',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: EditCode,
    exact: false,
    path: '/editcode/:id?/:editable?',
    title: 'DAG Code Editor',
  },
  {
    allowedRoles: UserAndAdminRole,
    component: Comingsoon,
    exact: false,
    path: '/comingsoon',
    title: 'Coming soon',
  },
  /******************************************************************
   * Following commented code will be uncomment after this PI release
   ******************************************************************/
  // {
  //   allowedRoles: UserAndAdminRole,
  //   component: Notifications,
  //   exact: false,
  //   path: '/notifications',
  //   title: 'Notifications',
  // },
];

export const routes = [...publicRoutes, ...protectedRoutes];

export class Routes extends React.Component<{}, {}> {
  public render() {
    const appName = getTranslatedLabel('HeaderName');
    document.title = appName;
    return (
      <React.Suspense fallback={<Progress show={true} />}>
        <Router history={history}>
          <Switch>
            {publicRoutes.map((route, index) => (
              <Route key={index} path={route.path} exact={route.exact} component={route.component} />
            ))}
            {protectedRoutes.map((route, index) => (
              // @ts-ignore: No overload matches this call.-
              <ProtectedRoute
                key={index}
                allowedRoles={route.allowedRoles}
                path={route.path}
                component={route.component}
                title={appName + ' - ' + route.title}
              />
            ))}
            <Route component={NotFoundPage} />
          </Switch>
        </Router>
      </React.Suspense>
    );
  }
}
