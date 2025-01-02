import { dev } from '$app/environment';
export const SPRING_BASE_URL = dev ? 'http://localhost:8080' : '';

import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';
import type { TransitionConfig } from 'svelte/transition';
import { cubicOut } from 'svelte/easing';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export interface Breadcrumb {
  text: string;
  link: string;
}

// Function to capitalize the first letter of a string
export function capitalizeFirstLetter(str: string): string {
  if (!str) return str; // Return the original string if it's empty
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

// Function to capitalize the first letter of each word in a sentence
export function capitalizeWords(sentence: string): string {
  return sentence
    .split(' ')
    .map((word) => capitalizeFirstLetter(word))
    .join(' ');
}

export function extractBreadcrumbs(pathname: string): Breadcrumb[] {
  if (pathname === '/' || pathname === '') {
    return [];
  }

  // Removing leading/trailing slashes and splitting
  const paths = pathname.replace(/^\/|\/$/g, '').split('/');
  let accumulatedPath = '';

  const breadcrumbs: Breadcrumb[] = paths
    .map((path) => {
      // Accumulate the path incrementally
      accumulatedPath += `/${path}`;

      // Filter out ULIDs for the text, but keep ULIDs for the link
      const text = isUlid(path)
        ? '' // If it's a ULID, keep the text empty
        : decodeURIComponent(path);

      return {
        text: text, // ULIDs won't affect the text, only non-ULID paths will
        link: accumulatedPath // Keep full path (including ULIDs) in the link
      };
    })
    .filter((crumb) => crumb.text !== ''); // Filter out breadcrumbs with empty text

  return breadcrumbs;
}

export const isUlid = (id: string): boolean => {
  return /^[0-9A-HJKMNP-TV-Z]{26}$/.test(id.toUpperCase());
};

type FlyAndScaleParams = {
  y?: number;
  x?: number;
  start?: number;
  duration?: number;
};

export function flyAndScale(
  node: Element,
  params: FlyAndScaleParams = { y: -8, x: 0, start: 0.95, duration: 150 }
): TransitionConfig {
  const style = getComputedStyle(node);
  const transform = style.transform === 'none' ? '' : style.transform;

  const scaleConversion = (valueA: number, scaleA: [number, number], scaleB: [number, number]) => {
    const [minA, maxA] = scaleA;
    const [minB, maxB] = scaleB;

    const percentage = (valueA - minA) / (maxA - minA);
    const valueB = percentage * (maxB - minB) + minB;

    return valueB;
  };

  return {
    duration: params.duration ?? 200,
    delay: 0,
    css: (t) => {
      const y = scaleConversion(t, [0, 1], [params.y ?? 5, 0]);
      const x = scaleConversion(t, [0, 1], [params.x ?? 0, 0]);
      const scale = scaleConversion(t, [0, 1], [params.start ?? 0.95, 1]);

      return styleToString({
        transform: `${transform} translate3d(${x}px, ${y}px, 0) scale(${scale})`,
        opacity: t
      });
    },
    easing: cubicOut
  };
}

export function styleToString(style: Record<string, number | string | undefined>): string {
  return Object.keys(style).reduce((str, key) => {
    if (style[key] === undefined) return str;
    return `${str}${key}:${style[key]};`;
  }, '');
}
