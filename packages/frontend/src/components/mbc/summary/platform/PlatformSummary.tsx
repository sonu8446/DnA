import cn from 'classnames';
import * as React from 'react';
import { IconTick } from '../../../../components/icons/IconTick';
import { IPortfolio, INotebookInfo, IUserInfo, IDataiku } from '../../../../globals/types';
import Styles from './PlatformSummary.scss';
const classNames = cn.bind(Styles);
import { getDateFromTimestamp } from '../../../../services/utils';
import { Envs } from '../../../../globals/Envs';

export interface ITeamProps {
  portfolio: IPortfolio;
  noteBookInfo: INotebookInfo;
  dataIkuInfo: IDataiku;
  dnaNotebookEnabled: boolean;
  dnaDataIkuProjectEnabled: boolean;
  notebookAndDataIkuNotEnabled: boolean;
  user: IUserInfo;
}

export default function PlatformSummary(props: ITeamProps) {
  const platformChips =
    props.portfolio?.platforms && props.portfolio?.platforms.length > 0
      ? props.portfolio?.platforms.map((chip: any, index: any) => {
          const lastIndex: boolean = index === props.portfolio?.platforms.length - 1;
          return (
            <React.Fragment key={index}>
              {chip.name}&nbsp;{!lastIndex && `\u002F\xa0`}
            </React.Fragment>
          );
        })
      : 'NA';
  const solOnCloud = props.portfolio?.solutionOnCloud ? <IconTick /> : 'NA';
  const usageOfDaimler = props.portfolio?.usesExistingInternalPlatforms ? <IconTick /> : 'NA';
  return (
    <React.Fragment>
      <div className={classNames(Styles.mainPanel, 'mainPanelSection')}>
        <div id="platformSummery" className={Styles.wrapper}>
          <h3>Compute</h3>
          <div>
            <div className={classNames(Styles.flexLayout)}>
              <div id="solutionOnCloud" className={classNames(Styles.solutionSection)}>
                <label className="input-label summary">Solution On Cloud</label>
                <br />
                <label>{solOnCloud}</label>
              </div>
              <div id="usageOfDaimlerInteral" className={classNames(Styles.solutionSection)}>
                <label className="input-label summary">Usage Of {Envs.DNA_COMPANYNAME} Platforms</label>
                <br />
                <label>{usageOfDaimler}</label>
                {(props.dnaNotebookEnabled || props.dnaDataIkuProjectEnabled) && (
                  <div className={classNames(Styles.jupeterCard, 'jupeterCard')}>
                    <div className={Styles.jupeterIcon}>
                      {props.dnaNotebookEnabled && <i className="icon mbc-icon jupyter " />}
                      {props.dnaDataIkuProjectEnabled && <i className="icon mbc-icon dataiku" />}
                    </div>
                    <div className={Styles.jupeterCardContent}>
                      <h6>
                        {(props.dnaNotebookEnabled && props.noteBookInfo.name) ||
                          (props.dnaDataIkuProjectEnabled && props.dataIkuInfo.name)}
                      </h6>
                      <label>
                        Created on{' '}
                        {getDateFromTimestamp(
                          (props.dnaNotebookEnabled && props.noteBookInfo.createdOn) ||
                            (props.dnaDataIkuProjectEnabled && props.dataIkuInfo.creationTag?.lastModifiedOn),
                          '.',
                        )}{' '}
                        by {props.user.firstName}
                      </label>
                      <div className={Styles.JuperterCardDesc}>
                        {(props.dnaNotebookEnabled && props.noteBookInfo.description) ||
                          (props.dnaDataIkuProjectEnabled && props.dataIkuInfo.shortDesc)}
                      </div>
                    </div>
                  </div>
                )}
              </div>
              <div id="platformTags" className={classNames(Styles.solutionSection)}>
                <label className="input-label summary">Platform</label>
                <br />
                <div className={classNames(Styles.row)}>{platformChips}</div>
              </div>
            </div>
          </div>
          {/* )} */}
        </div>
      </div>{' '}
    </React.Fragment>
  );
}
