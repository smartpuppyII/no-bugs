import type { RouteMeta } from 'vue-router'
import { Icon } from '@/components/Icon'
import { useI18n } from '@/hooks/web/useI18n'

export const useRenderMenuTitle = () => {
  const renderMenuTitle = (meta: RouteMeta, badge?: number) => {
    const { t } = useI18n()
    const { title = 'Please set title', icon } = meta
    const showBadge = badge !== undefined && badge > 0

    const titleEl = (
      <span class="v-menu__title overflow-hidden overflow-ellipsis whitespace-nowrap">
        {t(title as string)}
      </span>
    )

    const badgeEl = showBadge ? (
      <span class="menu-badge">{badge! > 99 ? '99+' : badge}</span>
    ) : null

    return icon ? (
      <>
        <Icon icon={meta.icon}></Icon>
        {titleEl}
        {badgeEl}
      </>
    ) : (
      <>
        {titleEl}
        {badgeEl}
      </>
    )
  }

  return {
    renderMenuTitle
  }
}
