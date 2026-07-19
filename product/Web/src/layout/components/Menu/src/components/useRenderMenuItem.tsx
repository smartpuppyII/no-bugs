import { ElSubMenu, ElMenuItem } from 'element-plus'
import { hasOneShowingChild } from '../helper'
import { isUrl } from '@/utils/is'
import { useRenderMenuTitle } from './useRenderMenuTitle'
import { pathResolve } from '@/utils/routerHelper'

const { renderMenuTitle } = useRenderMenuTitle()

export const useRenderMenuItem = () =>
  // allRouters: AppRouteRecordRaw[] = [],
  {
    const renderMenuItem = (
      routers: AppRouteRecordRaw[],
      parentPath = '/',
      badgeMap?: Record<string, number>
    ) => {
      return routers
        .filter((v) => !v.meta?.hidden)
        .map((v) => {
          const meta = v.meta ?? {}
          const { oneShowingChild, onlyOneChild } = hasOneShowingChild(v.children, v)
          const fullPath = isUrl(v.path) ? v.path : pathResolve(parentPath, v.path) // getAllParentPath<AppRouteRecordRaw>(allRouters, v.path).join('/')

          const getBadge = (path: string) =>
            badgeMap ? badgeMap[path] ?? 0 : 0

          if (
            oneShowingChild &&
            (!onlyOneChild?.children || onlyOneChild?.noShowingChildren) &&
            !meta?.alwaysShow
          ) {
            const itemPath = onlyOneChild ? pathResolve(fullPath, onlyOneChild.path) : fullPath
            const itemMeta = onlyOneChild ? onlyOneChild?.meta : meta
            return (
              <ElMenuItem index={itemPath}>
                {{
                  default: () => renderMenuTitle(itemMeta, getBadge(itemPath))
                }}
              </ElMenuItem>
            )
          } else {
            return (
              <ElSubMenu index={(v.name as string) || fullPath}>
                {{
                  title: () => renderMenuTitle(meta, getBadge(fullPath)),
                  default: () => renderMenuItem(v.children!, fullPath, badgeMap)
                }}
              </ElSubMenu>
            )
          }
        })
    }

    return {
      renderMenuItem
    }
  }
